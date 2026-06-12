package com.resolum.intiva.platform.finances.application.internal.commandhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalCategoriesService;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.commands.*;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.finances.domain.services.SpendingLimitCommandService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.SpendingLimitRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Coordinates commands for expense spending limits.
 *
 * <p>Creation validates the referenced target through ACL services because categories and financial accounts live in
 * the payment methods and categories bounded context. Consumption is triggered internally after EXPENSE transactions
 * are saved in the finances bounded context.</p>
 */
@Service
public class SpendingLimitCommandServiceImpl implements SpendingLimitCommandService {

    /**
     * Repository used to persist and retrieve spending limits.
     */
    private final SpendingLimitRepository spendingLimitRepository;

    /**
     * ACL service used to validate category targets in the payment methods and categories context.
     */
    private final FinancesExternalCategoriesService financesExternalCategoriesService;

    /**
     * ACL service used to validate financial account targets in the payment methods and categories context.
     */
    private final FinancesExternalFinancialAccountService financesExternalFinancialAccountService;

    /**
     * Creates the command service with the dependencies required for target validation and persistence.
     */
    public SpendingLimitCommandServiceImpl(
            SpendingLimitRepository spendingLimitRepository,
            FinancesExternalCategoriesService financesExternalCategoriesService,
            FinancesExternalFinancialAccountService financesExternalFinancialAccountService
    ) {
        this.spendingLimitRepository = spendingLimitRepository;
        this.financesExternalCategoriesService = financesExternalCategoriesService;
        this.financesExternalFinancialAccountService = financesExternalFinancialAccountService;
    }

    /**
     * Creates and stores a new spending limit after validating the referenced target.
     */
    @Override
    public Optional<SpendingLimit> handle(CreateSpendingLimitCommand command) {
        validateTarget(command.targetType(), command.targetId());

        var conflictingActiveLimits = spendingLimitRepository.findByOwnerIdAndOwnerTypeAndTargetTypeAndTargetIdAndActiveTrue(
                command.ownerId(),
                command.ownerType(),
                command.targetType(),
                command.targetId()
        );

        if (!conflictingActiveLimits.isEmpty()) {
            throw new IllegalArgumentException(
                    "An active spending limit already exists for the same target and an overlapping period."
            );
        }

        var spendingLimit = new SpendingLimit(command);
        return Optional.of(spendingLimitRepository.save(spendingLimit));
    }

    /**
     * Updates the amount of an existing spending limit.
     */
    @Override
    public Optional<SpendingLimit> handle(UpdateSpendingLimitAmountCommand command) {
        var spendingLimit = findSpendingLimit(command.spendingLimitId());
        spendingLimit.updateLimitAmount(command.newLimitAmount());
        return Optional.of(spendingLimitRepository.save(spendingLimit));
    }

    /**
     * Updates the period of an existing spending limit.
     */
    @Override
    public Optional<SpendingLimit> handle(UpdateSpendingLimitPeriodCommand command) {
        var spendingLimit = findSpendingLimit(command.spendingLimitId());
        spendingLimit.updatePeriod(command.startDate(), command.endDate());
        return Optional.of(spendingLimitRepository.save(spendingLimit));
    }

    /**
     * Activates an existing spending limit.
     */
    @Override
    public Optional<SpendingLimit> handle(ActivateSpendingLimitCommand command) {
        var spendingLimit = findSpendingLimit(command.spendingLimitId());
        spendingLimit.activate();
        return Optional.of(spendingLimitRepository.save(spendingLimit));
    }

    /**
     * Deactivates an existing spending limit.
     */
    @Override
    public Optional<SpendingLimit> handle(DeactivateSpendingLimitCommand command) {
        var spendingLimit = findSpendingLimit(command.spendingLimitId());
        spendingLimit.deactivate();
        return Optional.of(spendingLimitRepository.save(spendingLimit));
    }

    /**
     * Consumes every active spending limit that matches the provided expense transaction.
     */
    @Override
    public void handle(RegisterExpenseAgainstSpendingLimitsCommand command) {
        // Only active limits for the same owner are candidates. Target, period and currency are filtered below.
        var activeLimits = spendingLimitRepository.findByOwnerIdAndOwnerTypeAndActiveTrue(
                command.ownerId(),
                command.ownerType()
        );

        var applicableLimits = activeLimits.stream()
                .filter(limit -> limit.appliesTo(
                        command.ownerId(),
                        command.ownerType(),
                        command.categoryId(),
                        command.financialAccountId(),
                        command.transactionDate()
                ))
                .filter(limit -> limit.getLimitAmount().currencyCode().equals(command.amount().currencyCode()))
                .toList();

        applicableLimits.forEach(limit -> limit.registerExpense(command.amount()));
        spendingLimitRepository.saveAll(applicableLimits);
    }

    /**
     * Loads a spending limit or throws when it does not exist.
     *
     * @param spendingLimitId identifier to resolve
     * @return the matching spending limit
     */
    private SpendingLimit findSpendingLimit(Long spendingLimitId) {
        return spendingLimitRepository.findById(spendingLimitId)
                .orElseThrow(() -> new IllegalArgumentException("Spending limit with ID " + spendingLimitId + " does not exist."));
    }

    /**
     * Validates that the requested spending limit target exists in its owning bounded context.
     *
     * @param targetType target type being validated
     * @param targetId target identifier being validated
     */
    private void validateTarget(SpendingLimitTargetType targetType, Long targetId) {
        if (targetType == SpendingLimitTargetType.CATEGORY && !financesExternalCategoriesService.existsCategoryById(targetId)) {
            throw new IllegalArgumentException("Category with ID " + targetId + " does not exist.");
        }
        if (targetType == SpendingLimitTargetType.FINANCIAL_ACCOUNT && !financesExternalFinancialAccountService.existsFinancialAccountById(targetId)) {
            throw new IllegalArgumentException("Financial account with ID " + targetId + " does not exist.");
        }
    }
}

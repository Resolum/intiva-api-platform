package com.resolum.intiva.platform.finances.application.internal.commandhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalCategoriesService;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.finances.domain.model.commands.ActivateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.CreateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.DeactivateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.services.RecurringTransactionCommandService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Default application service for recurring transaction commands.
 *
 * <p>This service validates references that live outside the finances bounded context and delegates state changes
 * to the recurring transaction aggregate.</p>
 */
@Service
public class RecurringTransactionCommandServiceImpl implements RecurringTransactionCommandService {

    /**
     * Repository used to persist recurring transaction definitions.
     */
    private final RecurringTransactionRepository recurringTransactionRepository;

    /**
     * ACL used to validate category references before persisting definitions.
     */
    private final FinancesExternalCategoriesService financesExternalCategoriesService;

    /**
     * ACL used to validate financial account references before persisting definitions.
     */
    private final FinancesExternalFinancialAccountService financesExternalFinancialAccountService;

    /**
     * Creates the command service with its persistence and ACL dependencies.
     *
     * @param recurringTransactionRepository recurring transaction repository
     * @param financesExternalCategoriesService categories ACL
     * @param financesExternalFinancialAccountService financial accounts ACL
     */
    public RecurringTransactionCommandServiceImpl(
            RecurringTransactionRepository recurringTransactionRepository,
            FinancesExternalCategoriesService financesExternalCategoriesService,
            FinancesExternalFinancialAccountService financesExternalFinancialAccountService
    ) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.financesExternalCategoriesService = financesExternalCategoriesService;
        this.financesExternalFinancialAccountService = financesExternalFinancialAccountService;
    }

    /**
     * Validates external references and persists a new recurring transaction definition.
     *
     * @param command creation command
     * @return saved recurring transaction aggregate
     */
    @Override
    public Optional<RecurringTransaction> handle(CreateRecurringTransactionCommand command) {
        validateReferences(command);
        var recurringTransaction = new RecurringTransaction(command);
        return Optional.of(recurringTransactionRepository.save(recurringTransaction));
    }

    /**
     * Reactivates a recurring transaction definition.
     *
     * @param command activation command
     * @return updated recurring transaction aggregate
     */
    @Override
    public Optional<RecurringTransaction> handle(ActivateRecurringTransactionCommand command) {
        var recurringTransaction = findRecurringTransaction(command.recurringTransactionId());
        recurringTransaction.activate();
        return Optional.of(recurringTransactionRepository.save(recurringTransaction));
    }

    /**
     * Deactivates a recurring transaction definition.
     *
     * @param command deactivation command
     * @return updated recurring transaction aggregate
     */
    @Override
    public Optional<RecurringTransaction> handle(DeactivateRecurringTransactionCommand command) {
        var recurringTransaction = findRecurringTransaction(command.recurringTransactionId());
        recurringTransaction.deactivate();
        return Optional.of(recurringTransactionRepository.save(recurringTransaction));
    }

    /**
     * Loads one recurring transaction definition or fails with a business-friendly error.
     *
     * @param recurringTransactionId aggregate identifier
     * @return loaded aggregate
     */
    private RecurringTransaction findRecurringTransaction(Long recurringTransactionId) {
        return recurringTransactionRepository.findById(recurringTransactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Recurring transaction with ID " + recurringTransactionId + " does not exist."
                ));
    }

    /**
     * Validates external category and financial account references before creation.
     *
     * @param command create command carrying the referenced identifiers
     */
    private void validateReferences(CreateRecurringTransactionCommand command) {
        if (!financesExternalCategoriesService.existsCategoryById(command.categoryId().getValue())) {
            throw new IllegalArgumentException("Category with ID " + command.categoryId().getValue() + " does not exist.");
        }
        if (!financesExternalFinancialAccountService.existsFinancialAccountById(command.financialAccountId().getValue())) {
            throw new IllegalArgumentException("Financial account with ID " + command.financialAccountId().getValue() + " does not exist.");
        }
    }
}

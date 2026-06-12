package com.resolum.intiva.platform.finances.application.internal.commandhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalCategoriesService;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.finances.domain.model.commands.ActivateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.CreateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.DeactivateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdatePaymentReminderCommand;
import com.resolum.intiva.platform.finances.domain.services.RecurringTransactionCommandService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Default application service for recurring transaction commands.
 *
 * <p>This service validates references that live outside the finances bounded context and delegates state changes
 * to the recurring transaction aggregate.</p>
 */
@Service
@Slf4j
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
        log.info("Creating recurring transaction. description={}, ownerId={}, ownerType={}, frequency={}",
                command.description(), command.ownerId(), command.ownerType(), command.frequency());
        validateReferences(command);
        var recurringTransaction = new RecurringTransaction(command);
        var saved = recurringTransactionRepository.save(recurringTransaction);
        log.info("Recurring transaction created with ID={}", saved.getId());
        return Optional.of(saved);
    }

    /**
     * Reactivates a recurring transaction definition.
     *
     * @param command activation command
     * @return updated recurring transaction aggregate
     */
    @Override
    public Optional<RecurringTransaction> handle(ActivateRecurringTransactionCommand command) {
        log.info("Activating recurring transaction. recurringTransactionId={}", command.recurringTransactionId());
        var recurringTransaction = findRecurringTransaction(command.recurringTransactionId());
        recurringTransaction.activate();
        var saved = recurringTransactionRepository.save(recurringTransaction);
        log.info("Recurring transaction activated. recurringTransactionId={}", saved.getId());
        return Optional.of(saved);
    }

    /**
     * Deactivates a recurring transaction definition.
     *
     * @param command deactivation command
     * @return updated recurring transaction aggregate
     */
    @Override
    public Optional<RecurringTransaction> handle(DeactivateRecurringTransactionCommand command) {
        log.info("Deactivating recurring transaction. recurringTransactionId={}", command.recurringTransactionId());
        var recurringTransaction = findRecurringTransaction(command.recurringTransactionId());
        recurringTransaction.deactivate();
        var saved = recurringTransactionRepository.save(recurringTransaction);
        log.info("Recurring transaction deactivated. recurringTransactionId={}", saved.getId());
        return Optional.of(saved);
    }

    @Override
    public Optional<RecurringTransaction> handle(UpdatePaymentReminderCommand command) {
        log.info("Updating payment reminder. recurringTransactionId={}, reminderDaysBefore={}",
                command.recurringTransactionId(), command.reminderDaysBefore());
        var recurringTransaction = findRecurringTransaction(command.recurringTransactionId());
        recurringTransaction.updateReminderDays(command.reminderDaysBefore());
        var saved = recurringTransactionRepository.save(recurringTransaction);
        log.info("Payment reminder updated. recurringTransactionId={}, reminderDaysBefore={}",
                saved.getId(), saved.getReminderDaysBefore());
        return Optional.of(saved);
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

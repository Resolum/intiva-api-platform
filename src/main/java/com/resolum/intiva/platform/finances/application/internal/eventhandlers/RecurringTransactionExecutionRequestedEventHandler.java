package com.resolum.intiva.platform.finances.application.internal.eventhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.domain.model.commands.RegisterTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.events.RecurringTransactionExecutionRequestedEvent;
import com.resolum.intiva.platform.finances.domain.services.TransactionCommandService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Application event handler that turns a due recurring definition into a normal transaction when execution is valid.
 *
 * <p>For expense recurrences, the handler validates available balance through the financial-account ACL before
 * delegating to the normal transaction command flow.</p>
 */
@Service
public class RecurringTransactionExecutionRequestedEventHandler {

    /**
     * Logger used to record failed execution attempts.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecurringTransactionExecutionRequestedEventHandler.class);

    /**
     * Existing transaction command service reused to materialize recurring definitions into normal transactions.
     */
    private final TransactionCommandService transactionCommandService;

    /**
     * Financial account ACL used to validate expense balance before executing a recurring definition.
     */
    private final FinancesExternalFinancialAccountService financesExternalFinancialAccountService;

    /**
     * Repository used to persist recurring transaction schedule changes after a successful execution.
     */
    private final RecurringTransactionRepository recurringTransactionRepository;

    /**
     * Creates the event handler with the dependencies required to validate and execute recurring definitions.
     *
     * @param transactionCommandService transaction command service
     * @param financesExternalFinancialAccountService financial-account ACL
     * @param recurringTransactionRepository recurring transaction repository
     */
    public RecurringTransactionExecutionRequestedEventHandler(
            TransactionCommandService transactionCommandService,
            FinancesExternalFinancialAccountService financesExternalFinancialAccountService,
            RecurringTransactionRepository recurringTransactionRepository
    ) {
        this.transactionCommandService = transactionCommandService;
        this.financesExternalFinancialAccountService = financesExternalFinancialAccountService;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    /**
     * Validates one requested recurring execution and, when valid, materializes it into a normal transaction.
     *
     * @param event requested recurring execution event
     */
    @EventListener
    public void on(RecurringTransactionExecutionRequestedEvent event) {
        var recurringTransaction = event.getRecurringTransaction();

        if (recurringTransaction.getTransactionType() == TransactionTypes.EXPENSE
                && !financesExternalFinancialAccountService.hasSufficientBalance(
                recurringTransaction.getFinancialAccountId().getValue(),
                recurringTransaction.getAmount().amount()
        )) {
            LOGGER.warn(
                    "Recurring transaction {} skipped because of insufficient balance on financial account {}",
                    recurringTransaction.getId(),
                    recurringTransaction.getFinancialAccountId().getValue()
            );
            return;
        }

        var command = new RegisterTransactionCommand(
                recurringTransaction.getAmount(),
                recurringTransaction.getDescription(),
                recurringTransaction.getOwnerId(),
                recurringTransaction.getFinancialAccountId(),
                recurringTransaction.getPerformedByUserId(),
                recurringTransaction.getTransactionType(),
                recurringTransaction.getCategoryId(),
                recurringTransaction.getOwnerType(),
                null,
                null,
                null
        );

        transactionCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException(
                        "Recurring transaction execution did not produce a transaction."
                ));

        recurringTransaction.registerExecution();
        recurringTransactionRepository.save(recurringTransaction);
    }
}

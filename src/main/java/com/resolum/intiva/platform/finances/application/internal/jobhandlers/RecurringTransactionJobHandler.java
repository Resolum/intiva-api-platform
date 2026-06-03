package com.resolum.intiva.platform.finances.application.internal.jobhandlers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.finances.domain.model.events.RecurringTransactionExecutionRequestedEvent;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Application-level batch handler that materializes due recurring definitions into normal transactions.
 *
 * <p>The scheduler in infrastructure only triggers this handler. The handler loads due definitions and publishes
 * a domain event for each scheduled execution. The event handler then validates balance and materializes the
 * recurring definition into a normal transaction.</p>
 */
@Service
public class RecurringTransactionJobHandler {

    /**
     * Logger used to record execution errors without stopping the whole batch.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecurringTransactionJobHandler.class);

    /**
     * Repository used to locate due recurring definitions and persist their new schedule state.
     */
    private final RecurringTransactionRepository recurringTransactionRepository;

    /**
     * Event publisher used to emit one execution request event per scheduled recurring run.
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates the job handler with the persistence and transaction dependencies it needs.
     *
     * @param recurringTransactionRepository recurring transaction repository
     * @param eventPublisher event publisher
     */
    public RecurringTransactionJobHandler(
            RecurringTransactionRepository recurringTransactionRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Executes every recurring definition whose next execution date is due on or before today.
     *
     * <p>If the application was offline or missed previous runs, the handler catches up one definition as many
     * times as needed until its schedule moves beyond the current day.</p>
     */
    @Transactional
    public void executeDueRecurringTransactions() {
        var today = LocalDate.now();
        var dueRecurringTransactions =
                recurringTransactionRepository.findAllByActiveTrueAndNextExecutionDateLessThanEqual(today);

        for (var recurringTransaction : dueRecurringTransactions) {
            executeRecurringTransaction(recurringTransaction, today);
        }
    }

    /**
     * Executes one recurring definition repeatedly while it remains due.
     *
     * @param recurringTransaction aggregate being executed
     * @param today scheduler evaluation date
     */
    private void executeRecurringTransaction(RecurringTransaction recurringTransaction, LocalDate today) {
        while (recurringTransaction.isDue(today)) {
            try {
                var previousExecutionDate = recurringTransaction.getNextExecutionDate();
                eventPublisher.publishEvent(new RecurringTransactionExecutionRequestedEvent(this, recurringTransaction));

                if (previousExecutionDate.equals(recurringTransaction.getNextExecutionDate())) {
                    return;
                }
            } catch (Exception exception) {
                LOGGER.error(
                        "Error while executing recurring transaction {}: {}",
                        recurringTransaction.getId(),
                        exception.getMessage()
                );
                return;
            }
        }
    }
}

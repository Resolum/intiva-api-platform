package com.resolum.intiva.platform.finances.infrastructure.scheduling.jobs;

import com.resolum.intiva.platform.finances.application.internal.jobhandlers.RecurringTransactionJobHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Infrastructure scheduler that periodically triggers recurring transaction execution.
 *
 * <p>This class owns the technical timing concern only. Business orchestration remains inside the application
 * layer job handler.</p>
 */
@Component
public class RecurringTransactionScheduler {

    /**
     * Application handler responsible for executing due recurring definitions.
     */
    private final RecurringTransactionJobHandler recurringTransactionJobHandler;

    /**
     * Creates the scheduler with the application handler it should trigger.
     *
     * @param recurringTransactionJobHandler recurring transaction job handler
     */
    public RecurringTransactionScheduler(RecurringTransactionJobHandler recurringTransactionJobHandler) {
        this.recurringTransactionJobHandler = recurringTransactionJobHandler;
    }

    /**
     * Runs every five minutes and delegates the recurring execution batch to the application layer.
     */
    @Scheduled(cron = "0 */5 * * * *")
    public void runDueRecurringTransactions() {
        recurringTransactionJobHandler.executeDueRecurringTransactions();
    }
}

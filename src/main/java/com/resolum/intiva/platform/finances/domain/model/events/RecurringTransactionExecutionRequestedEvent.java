package com.resolum.intiva.platform.finances.domain.model.events;

import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain event published when the recurring-transactions scheduler wants to materialize one scheduled execution.
 *
 * <p>The event keeps the recurring transaction aggregate as the source of truth while delegating orchestration
 * concerns such as balance validation and transaction registration to an application event handler.</p>
 */
@Getter
public class RecurringTransactionExecutionRequestedEvent extends ApplicationEvent {

    /**
     * Recurring transaction definition that is about to be executed.
     */
    private final RecurringTransaction recurringTransaction;

    /**
     * Creates a new recurring-execution request event.
     *
     * @param source event source
     * @param recurringTransaction recurring definition selected by the scheduler
     */
    public RecurringTransactionExecutionRequestedEvent(Object source, RecurringTransaction recurringTransaction) {
        super(source);
        this.recurringTransaction = recurringTransaction;
    }
}

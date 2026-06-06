package com.resolum.intiva.platform.finances.domain.services;

import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.finances.domain.model.commands.ActivateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.CreateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.DeactivateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdatePaymentReminderCommand;

import java.util.Optional;

/**
 * Application-facing command contract for recurring transaction definitions.
 */
public interface RecurringTransactionCommandService {

    /**
     * Creates a recurring transaction definition.
     *
     * @param command creation input
     * @return created aggregate when successful
     */
    Optional<RecurringTransaction> handle(CreateRecurringTransactionCommand command);

    /**
     * Reactivates a recurring transaction definition.
     *
     * @param command activation input
     * @return updated aggregate when successful
     */
    Optional<RecurringTransaction> handle(ActivateRecurringTransactionCommand command);

    /**
     * Deactivates a recurring transaction definition.
     *
     * @param command deactivation input
     * @return updated aggregate when successful
     */
    Optional<RecurringTransaction> handle(DeactivateRecurringTransactionCommand command);

    /**
     * Updates the reminder configuration for a recurring transaction definition.
     *
     * @param command reminder update input
     * @return updated aggregate when successful
     */
    Optional<RecurringTransaction> handle(UpdatePaymentReminderCommand command);
}

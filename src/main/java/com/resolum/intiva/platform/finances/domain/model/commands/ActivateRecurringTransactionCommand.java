package com.resolum.intiva.platform.finances.domain.model.commands;

/**
 * Command that reactivates a recurring transaction definition.
 *
 * @param recurringTransactionId identifier of the recurring transaction to activate
 */
public record ActivateRecurringTransactionCommand(Long recurringTransactionId) {
}

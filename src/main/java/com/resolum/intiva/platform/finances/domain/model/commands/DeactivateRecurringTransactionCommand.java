package com.resolum.intiva.platform.finances.domain.model.commands;

/**
 * Command that deactivates a recurring transaction definition.
 *
 * @param recurringTransactionId identifier of the recurring transaction to deactivate
 */
public record DeactivateRecurringTransactionCommand(Long recurringTransactionId) {
}

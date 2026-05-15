package com.resolum.intiva.platform.finances.interfaces.rest.resources.requests;

/**
 * Command to update the description of a transaction entry.
 * @param transactionId the ID of the transaction entry to update
 * @param description the new description for the transaction entry
 */
public record UpdateTransactionDescriptionCommand(
        Long transactionId,
        String description
) {
}

package com.resolum.intiva.platform.finances.interfaces.rest.resources.requests;

/**
 * Command to update the description of a transaction entry.
 * @param description the new description for the transaction entry
 */
public record UpdateTransactionDescriptionResource(
        String description
) {
}

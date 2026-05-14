package com.resolum.intiva.platform.finances.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionEntryId;

/**
 * Command to update the description of a transaction entry.
 * @param transactionId the ID of the transaction entry to update
 * @param description the new description for the transaction entry
 */
public record UpdateTransactionDescriptionCommand(
        TransactionEntryId transactionId,
        String description
) {
}

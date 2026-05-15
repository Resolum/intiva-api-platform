package com.resolum.intiva.platform.finances.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionEntryId;
import jakarta.validation.Valid;

/**
 * Query to retrieve a transaction by its ID.
 * @param transactionId The ID of the transaction to retrieve.
 */
public record GetTransactionByIdQuery(@Valid TransactionEntryId transactionId) {
}

package com.resolum.intiva.platform.finances.domain.model.queries;

import com.resolum.intiva.platform.finances.domain.model.valueobjects.TransactionTypes;

/**
 * Query to get transactions by owner ID and transaction type.
 * @param ownerId the ID of the owner whose transactions are to be retrieved.
 * @param transactionType the type of transactions to be retrieved (e.g., INCOME, EXPENSE).
 */
public record GetTransactionsByOwnerIdAndTransactionTypeQuery(String ownerId, TransactionTypes transactionType) {
}

package com.resolum.intiva.platform.finances.domain.model.valueobjects;

import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;

/**
 * Record representing a transaction along with the visible name of its financial account.
 *
 * @param transaction the financial transaction data
 * @param financialAccountName the name of the financial account associated with the transaction
 */
public record TransactionWithFinancialAccountName(
        Transaction transaction,
        String financialAccountName
) {
}

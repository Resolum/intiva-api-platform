package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command object for creating a financial account transaction.
 * This command encapsulates the necessary information to create a transaction on a financial account, including the amount, currency code, and transaction type.
 *
 * @param amount the amount of the transaction
 * @param currencyCode the currency code of the transaction (e.g., "USD", "EUR")
 * @param transactionType the type of the transaction (e.g., "INCOME", "EXPENSE")
 */
public record CreateFinancialAccountTransaction(
        Long financialAccountId,
        BigDecimal amount,
        String currencyCode,
        String transactionType
) {
}

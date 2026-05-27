package com.resolum.intiva.platform.shared.domain.valueobjects;

/**
 * Enum representing the types of transactions in the financial system.
 * It can be either INCOME, EXPENSE or TRANSFER.
 * This enum is used to categorize transactions and helps in filtering and analyzing financial data based on the nature of the transactions.
 */
public enum TransactionTypes {
    INCOME,
    EXPENSE,
    TRANSFER;

    /**
     * Converts a string representation of a transaction type to its corresponding enum value.
     * The input string is case-insensitive, and if it does not match any of the defined transaction types, an IllegalArgumentException is thrown.
     *
     * @param value the string representation of the transaction type (e.g., "income", "expense", "transfer")
     * @return the corresponding TransactionTypes enum value
     * @throws IllegalArgumentException if the input string does not match any of the defined transaction types
     */
    public static TransactionTypes fromString(String value) {
        try {
            return TransactionTypes.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid currency code."
            );
        }
    }
}

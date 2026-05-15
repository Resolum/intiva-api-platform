package com.resolum.intiva.platform.finances.interfaces.rest.resources.responses;

/**
 * TransactionResource is a record that represents the data structure of a financial transaction as it is exposed through the REST API. It contains fields that correspond to the properties of a Transaction entity, including the amount, currency code, description, owner ID, financial account ID, actor user ID, transaction type, category ID, and the date and time when the transaction was registered.
 * @param amount The amount of the transaction, represented as a string to maintain precision and avoid issues with floating-point representation. It should be a valid decimal number in string format.
 * @param currencyCode The ISO 4217 currency code associated with the transaction, such as "USD" for US dollars or "EUR" for euros. This field is mandatory and must be a valid currency code.
 * @param description A brief description of the transaction, providing context and details about the nature of the transaction. This field is mandatory and cannot be null or blank.
 * @param ownerId The identifier of the owner of the transaction, represented as a string. This field is mandatory and must not be null or blank, as it is essential for associating the transaction with a specific user or account.
 * @param financialAccountId The identifier of the financial account associated with the transaction, represented as a long value. This field is mandatory and must correspond to an existing financial account in the system.
 * @param actorUserId The identifier of the user who performed the transaction, represented as a string. This field is mandatory and must not be null or blank, as it is important for tracking the user responsible for the transaction.
 * @param transactionType The type of the transaction, indicating whether it is an income or an expense. This field is mandatory and must be a valid value corresponding to the TransactionTypes enum, such as "INCOME" or "EXPENSE".
 * @param categoryId The identifier of the category associated with the transaction, represented as a long value. This field is optional and can be null if the transaction does not belong to any category. If provided, it must correspond to an existing category in the system.
 * @param registeredAt The date and time when the transaction was registered, represented as a string in ISO 8601 format. This field is mandatory and must be a valid date-time string, such as "2024-06-01T12:00:00Z".
 */
public record TransactionResource(
        String amount,
        String currencyCode,
        String description,
        String ownerId,
        Long financialAccountId,
        String actorUserId,
        String transactionType,
        Long categoryId,
        String registeredAt
) {
}

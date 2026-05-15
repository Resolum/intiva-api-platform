package com.resolum.intiva.platform.finances.interfaces.rest.resources.requests;

/**
 * UpdateTransactionAmountResource is a record that represents the data structure for updating the amount of an existing financial transaction through the REST API. It contains fields for the transaction ID, the new amount, and the currency code associated with the transaction.
 * @param amount The new amount for the transaction, represented as a string to maintain precision and avoid issues with floating-point representation. It should be a valid decimal number in string format and must not be null or blank.
 * @param currencyCode The ISO 4217 currency code associated with the transaction, such as "USD" for US dollars or "EUR" for euros. This field is mandatory and must be a valid currency code. It is important to ensure that the currency code is consistent with the original transaction's currency to avoid discrepancies in financial reporting and calculations.
 */
public record UpdateTransactionAmountResource(
        String amount,
        String currencyCode
) {
}

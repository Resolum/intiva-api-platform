package com.resolum.intiva.platform.finances.interfaces.rest.resources.responses;

import java.time.LocalDate;
import java.util.List;

/**
 * TransactionGroupByDateResource is a record that represents a financial transaction grouped by date. It contains two fields: 'date', which represents the date of the transaction, and 'transaction', which is a TransactionResource representing the details of the transaction that occurred on that date. This resource is used to structure the response when transactions are grouped by their respective dates in the REST API.
 * @param date The date of the transaction, represented as a LocalDate. This field is mandatory and must be a valid date.
 * @param transactions A list of TransactionResource objects that represent the details of the transactions that occurred on the specified date. Each TransactionResource contains information such as the amount, currency code, description, owner ID, financial account ID, actor user ID, transaction type, category ID, and the date and time when the transaction was registered. This field is mandatory and must contain at least one TransactionResource for the specified date.
 */
public record TransactionGroupByDateResource(
        LocalDate date,
        List<TransactionWithCategoryDesignResource> transactions
) {
}

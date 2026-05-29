package com.resolum.intiva.platform.finances.domain.model.valueobjects;

import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;

/**
 * Record representing a transaction along with its associated category design details, such as color and icon.
 * This record is used to encapsulate the transaction data along with the visual design elements of its category for presentation in REST responses.
 *
 * @param transaction the financial transaction data
 * @param categoryColor the color associated with the transaction's category
 * @param categoryIcon the icon associated with the transaction's category
 */
public record TransactionWithCategoryDesign(
        Transaction transaction,
        String categoryColor,
        String categoryIcon
) {
}

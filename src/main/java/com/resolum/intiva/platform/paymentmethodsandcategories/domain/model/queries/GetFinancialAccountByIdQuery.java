package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries;

/**
 * GetFinancialAccountByIdQuery is a simple record that represents a query to retrieve a financial account by its unique identifier.
 * It contains a single field, financialAccountId, which is the ID of the financial account to be retrieved.
 * This query can be used in the application layer to request the necessary information about a specific financial account from the database or any other data source.
 */
public record GetFinancialAccountByIdQuery(Long financialAccountId) {
}

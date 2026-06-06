package com.resolum.intiva.platform.categories.domain.model.queries;

/**
 * Query object for retrieving a financial account by its owner ID.
 * This class is used to encapsulate the parameters needed to perform the query.
 * It contains a single field, ownerId, which is the ID of the owner of the financial account.
 * The record keyword is used to create an immutable data class with a concise syntax.
 */
public record GetFinancialAccountByOwnerId(Long ownerId) {
}

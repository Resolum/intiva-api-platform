package com.resolum.intiva.platform.finances.domain.model.queries;

/**
 * Query used to retrieve a spending limit by its identifier.
 *
 * @param spendingLimitId unique identifier of the spending limit
 */
public record GetSpendingLimitByIdQuery(Long spendingLimitId) {
}

package com.resolum.intiva.platform.finances.domain.model.queries;

/**
 * Query used to retrieve one recurring transaction definition by id.
 *
 * @param recurringTransactionId recurring transaction identifier
 */
public record GetRecurringTransactionByIdQuery(Long recurringTransactionId) {
}

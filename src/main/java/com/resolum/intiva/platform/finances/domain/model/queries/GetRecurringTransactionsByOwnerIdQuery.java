package com.resolum.intiva.platform.finances.domain.model.queries;

/**
 * Query used to retrieve recurring transaction definitions for one owner regardless of owner type.
 *
 * @param ownerId owner identifier used as filter
 */
public record GetRecurringTransactionsByOwnerIdQuery(Long ownerId) {
}

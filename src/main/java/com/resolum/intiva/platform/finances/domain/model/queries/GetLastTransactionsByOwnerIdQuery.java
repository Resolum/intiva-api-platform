package com.resolum.intiva.platform.finances.domain.model.queries;

/**
 * Query to get the last transactions by owner id
 *
 * @param ownerId the owner id
 */
public record GetLastTransactionsByOwnerIdQuery(Long ownerId) {
}

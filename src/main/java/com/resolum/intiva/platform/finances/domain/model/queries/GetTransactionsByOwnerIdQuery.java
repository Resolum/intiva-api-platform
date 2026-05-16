package com.resolum.intiva.platform.finances.domain.model.queries;

/**
 * Query to get transactions by owner ID.
 * With this, users or families can retrieve all transactions associated with a specific owner, allowing them to view their financial activities and manage their finances effectively.
 * @param ownerId the ID of the owner whose transactions are to be retrieved.
 */
public record GetTransactionsByOwnerIdQuery(String ownerId) {
}

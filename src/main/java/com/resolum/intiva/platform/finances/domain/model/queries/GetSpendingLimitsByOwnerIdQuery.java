package com.resolum.intiva.platform.finances.domain.model.queries;

/**
 * Query used to retrieve every spending limit that belongs to an owner.
 *
 * @param ownerId user id or group id depending on the owner scope
 */
public record GetSpendingLimitsByOwnerIdQuery(Long ownerId) {
}

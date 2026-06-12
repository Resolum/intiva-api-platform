package com.resolum.intiva.platform.household.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Query to retrieve all family groups owned by a specific user.
 *
 * @param ownerId the UserId of the owner
 */
public record GetFamiliesByOwnerIdQuery(UserId ownerId) {
}

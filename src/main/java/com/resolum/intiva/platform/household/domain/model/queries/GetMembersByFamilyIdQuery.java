package com.resolum.intiva.platform.household.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Query to retrieve all active members of a family group.
 *
 * @param familyId    the ID of the family group
 * @param requesterId the UserId of the person making the request (must be a member)
 */
public record GetMembersByFamilyIdQuery(Long familyId, UserId requesterId) {
}

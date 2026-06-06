package com.resolum.intiva.platform.household.domain.model.queries;

/**
 * Query to retrieve a specific family member by their ID within a family group.
 *
 * @param memberId the ID of the member to retrieve
 * @param familyId the ID of the family group the member belongs to
 */
public record GetMemberByIdQuery(Long memberId, Long familyId) {
}

package com.resolum.intiva.platform.household.domain.model.queries;

/**
 * Query to retrieve a family group by its unique identifier.
 *
 * @param familyId the ID of the family group to retrieve
 */
public record GetFamilyByIdQuery(Long familyId) {
}

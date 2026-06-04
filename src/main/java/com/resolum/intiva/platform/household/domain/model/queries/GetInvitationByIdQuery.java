package com.resolum.intiva.platform.household.domain.model.queries;

/**
 * Query to retrieve a family group invitation by its unique identifier.
 *
 * @param invitationId the ID of the invitation to retrieve
 */
public record GetInvitationByIdQuery(Long invitationId) {
}

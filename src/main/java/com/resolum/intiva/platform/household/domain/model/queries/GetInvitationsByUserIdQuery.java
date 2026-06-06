package com.resolum.intiva.platform.household.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Query to retrieve all invitations for a specific user.
 *
 * @param userId the UserId of the invited person
 */
public record GetInvitationsByUserIdQuery(UserId userId) {
}

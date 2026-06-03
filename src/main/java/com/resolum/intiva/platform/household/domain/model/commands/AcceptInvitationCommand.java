package com.resolum.intiva.platform.household.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Command to accept a pending family group invitation.
 *
 * @param invitationId the ID of the invitation to accept
 * @param userId       the UserId of the user accepting the invitation
 */
public record AcceptInvitationCommand(
        Long invitationId,
        UserId userId
) {
}

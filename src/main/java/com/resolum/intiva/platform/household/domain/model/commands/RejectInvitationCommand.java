package com.resolum.intiva.platform.household.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Command to reject a pending family group invitation.
 *
 * @param invitationId the ID of the invitation to reject
 * @param userId       the UserId of the user rejecting the invitation
 */
public record RejectInvitationCommand(
        Long invitationId,
        UserId userId
) {
}

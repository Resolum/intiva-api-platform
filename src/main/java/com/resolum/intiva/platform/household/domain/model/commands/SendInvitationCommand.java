package com.resolum.intiva.platform.household.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

public record SendInvitationCommand(
        Long familyId,
        UserId invitedBy,
        UserId userInvitedId
) {
}

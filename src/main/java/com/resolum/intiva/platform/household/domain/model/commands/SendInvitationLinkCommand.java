package com.resolum.intiva.platform.household.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

public record SendInvitationLinkCommand(
        Long familyId,
        UserId inviterId,
        String inviteeEmail
) {
}

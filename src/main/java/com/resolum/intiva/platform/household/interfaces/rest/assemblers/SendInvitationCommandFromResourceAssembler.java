package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.commands.SendInvitationCommand;
import com.resolum.intiva.platform.household.interfaces.rest.resources.requests.SendInvitationResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

public class SendInvitationCommandFromResourceAssembler {

    public static SendInvitationCommand toCommandFromResource(SendInvitationResource resource, Long familyId, Long invitedBy) {
        return new SendInvitationCommand(
                familyId,
                new UserId(invitedBy),
                resource.userInvitedId() != null ? new UserId(resource.userInvitedId()) : null
        );
    }
}

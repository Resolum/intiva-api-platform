package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.commands.SendInvitationLinkCommand;
import com.resolum.intiva.platform.household.interfaces.rest.resources.requests.SendInvitationLinkResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

public class SendInvitationLinkCommandFromResourceAssembler {

    public static SendInvitationLinkCommand toCommandFromResource(SendInvitationLinkResource resource, Long inviterId) {
        return new SendInvitationLinkCommand(
                resource.familyId(),
                new UserId(inviterId),
                resource.inviteeEmail()
        );
    }
}

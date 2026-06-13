package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.application.internal.InvitationLinkResult;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.InvitationLinkResource;

public class InvitationLinkResourceFromEntityAssembler {

    public static InvitationLinkResource toResourceFromResult(InvitationLinkResult result) {
        return new InvitationLinkResource(
                result.token(),
                result.inviteUrl(),
                result.expiresAt() != null ? result.expiresAt().toString() : null
        );
    }
}

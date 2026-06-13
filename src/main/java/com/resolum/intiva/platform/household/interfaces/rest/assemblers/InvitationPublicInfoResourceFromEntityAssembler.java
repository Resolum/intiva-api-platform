package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.application.internal.InvitationPublicInfo;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.InvitationPublicInfoResource;

public class InvitationPublicInfoResourceFromEntityAssembler {

    public static InvitationPublicInfoResource toResourceFromInfo(InvitationPublicInfo info) {
        return new InvitationPublicInfoResource(
                info.groupName(),
                info.inviterName(),
                info.memberCount(),
                info.status(),
                info.expiresAt() != null ? info.expiresAt().toString() : null
        );
    }
}

package com.resolum.intiva.platform.household.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body to send a family group invitation link.")
public record SendInvitationLinkResource(

        @Schema(description = "Family group identifier.", example = "1")
        Long familyId,

        @Schema(description = "Optional email of the person being invited.", example = "user@example.com", nullable = true)
        String inviteeEmail
) {
}

package com.resolum.intiva.platform.household.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body to send a family group invitation.")
public record SendInvitationResource(

        @Schema(description = "User identifier of the person being invited (optional).", example = "99", nullable = true)
        Long userInvitedId
) {
}

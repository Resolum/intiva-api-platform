package com.resolum.intiva.platform.household.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "REST response with public invitation information.")
public record InvitationPublicInfoResource(

        @Schema(description = "Family group name.", example = "Mi Familia")
        String groupName,

        @Schema(description = "Name or identifier of the person who sent the invitation.", example = "42")
        String inviterName,

        @Schema(description = "Current number of active members in the family group.", example = "3")
        int memberCount,

        @Schema(description = "Invitation status.", example = "PENDING", allowableValues = {"PENDING", "ACCEPTED", "REJECTED", "EXPIRED"})
        String status,

        @Schema(description = "Date and time when the invitation expires.", example = "2026-06-19T10:30:00")
        String expiresAt
) {
}

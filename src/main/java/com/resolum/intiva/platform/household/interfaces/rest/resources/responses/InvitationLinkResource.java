package com.resolum.intiva.platform.household.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "REST response containing the invitation link details.")
public record InvitationLinkResource(

        @Schema(description = "Unique invitation token.", example = "550e8400-e29b-41d4-a716-446655440000")
        String token,

        @Schema(description = "Full invitation URL.", example = "https://yourapp.com/invite?token=...&group=MiFamilia&inviter=42")
        String inviteUrl,

        @Schema(description = "Date and time when the invitation expires.", example = "2026-06-19T10:30:00")
        String expiresAt
) {
}

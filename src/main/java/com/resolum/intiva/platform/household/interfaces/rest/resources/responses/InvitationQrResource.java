package com.resolum.intiva.platform.household.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "REST response containing the QR code for a family group invitation link. " +
        "The link encodes the unique invitation token and is valid for 7 days.")
public record InvitationQrResource(

        @Schema(description = "Unique invitation token used in the link.",
                example = "550e8400-e29b-41d4-a716-446655440000")
        String token,

        @Schema(description = "QR code image encoded as a Base64 string (PNG format, 250x250).")
        String qrBase64,

        @Schema(description = "Full invitation URL that the QR code encodes.",
                example = "https://intiva.app/join?token=550e8400-e29b-41d4-a716-446655440000")
        String invitationLink,

        @Schema(description = "Date and time when the invitation expires (7 days from sent date).",
                example = "2026-06-18T12:00:00")
        String expiresAt
) {
}

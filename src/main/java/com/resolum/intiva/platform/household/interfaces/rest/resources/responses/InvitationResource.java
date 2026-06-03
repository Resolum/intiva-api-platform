package com.resolum.intiva.platform.household.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST response resource representing a family group invitation.
 *
 * @param id            the unique identifier of the invitation
 * @param token         the unique token used for link or QR sharing
 * @param status        the current status: PENDING, ACCEPTED, or REJECTED
 * @param sentAt        the date and time when the invitation was sent
 * @param expiresAt     the date and time when the invitation expires
 * @param respondedAt   the date and time when the user responded, null if not yet responded
 * @param invitedBy     the user identifier of the person who sent the invitation
 * @param familyId      the family group identifier the invitation is for
 * @param userInvitedId the user identifier of the person being invited
 * @param isExpired     whether the invitation has passed its expiry date
 */
@Schema(description = "REST representation of a family group invitation.")
public record InvitationResource(
        @Schema(description = "Unique invitation identifier.", example = "1")
        Long id,

        @Schema(description = "Unique token for the invitation.", example = "550e8400-e29b-41d4-a716-446655440000")
        String token,

        @Schema(description = "Invitation status.", example = "PENDING", allowableValues = {"PENDING", "ACCEPTED", "REJECTED"})
        String status,

        @Schema(description = "Date and time when the invitation was sent.", example = "2026-06-01T10:30:00")
        String sentAt,

        @Schema(description = "Date and time when the invitation expires.", example = "2026-06-08T10:30:00")
        String expiresAt,

        @Schema(description = "Date and time when the invitation was responded to. Null if not yet responded.", example = "2026-06-02T15:00:00")
        String respondedAt,

        @Schema(description = "User identifier of who sent the invitation.", example = "42")
        Long invitedBy,

        @Schema(description = "Family group identifier the invitation belongs to.", example = "1")
        Long familyId,

        @Schema(description = "User identifier of who received the invitation.", example = "99")
        Long userInvitedId,

        @Schema(description = "Whether the invitation has expired.", example = "false")
        boolean isExpired
) {
}

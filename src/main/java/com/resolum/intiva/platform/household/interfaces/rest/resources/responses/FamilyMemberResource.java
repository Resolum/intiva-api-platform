package com.resolum.intiva.platform.household.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST response resource representing a family group member.
 *
 * @param id       the unique identifier of the member record
 * @param userId   the user identifier
 * @param familyId the family group identifier
 * @param role     the member's role: ADMIN or MEMBER
 * @param status   the member's status: ACTIVE or EXPELLED
 * @param joinedAt the date and time when the member joined the group
 */
@Schema(description = "REST representation of a family group member.")
public record FamilyMemberResource(
        @Schema(description = "Unique member identifier.", example = "1")
        Long id,

        @Schema(description = "User identifier.", example = "42")
        Long userId,

        @Schema(description = "Family group identifier.", example = "1")
        Long familyId,

        @Schema(description = "Member role.", example = "MEMBER", allowableValues = {"ADMIN", "MEMBER"})
        String role,

        @Schema(description = "Member status.", example = "ACTIVE", allowableValues = {"ACTIVE", "EXPELLED"})
        String status,

        @Schema(description = "Date and time when the member joined the group.", example = "2026-06-01T10:30:00Z")
        String joinedAt
) {
}

package com.resolum.intiva.platform.household.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST response resource representing a family group.
 *
 * @param id            the unique identifier of the family group
 * @param name          the name of the family group
 * @param description   an optional description of the family group
 * @param status        the current status: ACTIVE or DISOLVED
 * @param ownerId       the user identifier of the owner
 * @param membersActive the number of active members in the group
 * @param createdAt     the date and time when the family group was created
 */
@Schema(description = "REST representation of a family group.")
public record FamilyResource(
        @Schema(description = "Unique family identifier.", example = "1")
        Long id,

        @Schema(description = "Family group name.", example = "Mi Familia")
        String name,

        @Schema(description = "Family group description.", example = "Grupo familiar principal")
        String description,

        @Schema(description = "Family status.", example = "ACTIVE", allowableValues = {"ACTIVE", "DISOLVED"})
        String status,

        @Schema(description = "Owner user identifier.", example = "42")
        Long ownerId,

        @Schema(description = "Number of active members in the family group.", example = "1")
        int membersActive,

        @Schema(description = "Date and time when the family group was created.", example = "2026-06-01T10:30:00Z")
        String createdAt
) {
}

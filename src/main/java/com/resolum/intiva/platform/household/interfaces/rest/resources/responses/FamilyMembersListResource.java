package com.resolum.intiva.platform.household.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * REST response resource representing a list of family group members.
 *
 * @param familyId     the family group identifier
 * @param members      the list of active members
 * @param totalMembers the total number of active members
 * @param isEmpty      whether the group has no active members
 */
@Schema(description = "REST representation of a list of family group members.")
public record FamilyMembersListResource(
        @Schema(description = "Family group identifier.", example = "1")
        Long familyId,

        @Schema(description = "List of active family members.")
        List<FamilyMemberResource> members,

        @Schema(description = "Total number of active members.", example = "3")
        int totalMembers,

        @Schema(description = "Whether the family group has no active members.", example = "false")
        boolean isEmpty
) {
}

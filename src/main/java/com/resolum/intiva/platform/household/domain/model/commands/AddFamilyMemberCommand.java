package com.resolum.intiva.platform.household.domain.model.commands;

import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyRole;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Command to add a member to an existing family group.
 *
 * @param familyId the ID of the family group
 * @param userId   the UserId of the user to add
 * @param role     the role to assign to the new member
 */
public record AddFamilyMemberCommand(
        Long familyId,
        UserId userId,
        FamilyRole role
) {
}

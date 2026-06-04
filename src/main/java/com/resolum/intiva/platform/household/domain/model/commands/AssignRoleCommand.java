package com.resolum.intiva.platform.household.domain.model.commands;

import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyRole;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Command to assign a new role to a family group member.
 *
 * @param familyId       the ID of the family group
 * @param targetMemberId the ID of the member whose role is being changed
 * @param requesterId    the UserId of the ADMIN performing the role assignment
 * @param newRole        the new role to assign
 */
public record AssignRoleCommand(
        Long familyId,
        Long targetMemberId,
        UserId requesterId,
        FamilyRole newRole
) {
}

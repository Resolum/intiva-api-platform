package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.commands.AssignRoleCommand;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyRole;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Assembler that creates an AssignRoleCommand from path and request data.
 */
public class AssignRoleCommandFromResourceAssembler {

    /**
     * Converts path and request data into an AssignRoleCommand.
     *
     * @param familyId       the ID of the family group, taken from the path variable
     * @param targetMemberId the ID of the target member, taken from the path variable
     * @param newRole        the new role string to assign, converted to FamilyRole enum
     * @param requesterId    the numeric user ID of the ADMIN performing the assignment
     * @return the corresponding AssignRoleCommand
     */
    public static AssignRoleCommand toCommandFromResource(Long familyId, Long targetMemberId, String newRole, Long requesterId) {
        return new AssignRoleCommand(familyId, targetMemberId, new UserId(requesterId), FamilyRole.valueOf(newRole.toUpperCase()));
    }
}

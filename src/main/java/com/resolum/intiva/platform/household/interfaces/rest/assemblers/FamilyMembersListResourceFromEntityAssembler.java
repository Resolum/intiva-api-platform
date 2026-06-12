package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.FamilyMemberResource;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.FamilyMembersListResource;

import java.util.List;

/**
 * Assembler that converts a list of FamilyMember aggregates into a FamilyMembersListResource.
 */
public class FamilyMembersListResourceFromEntityAssembler {

    /**
     * Converts a list of FamilyMember entities into a FamilyMembersListResource.
     *
     * @param familyId the ID of the family group
     * @param members  the list of active members to convert
     * @return the corresponding FamilyMembersListResource
     */
    public static FamilyMembersListResource toResourceFromEntityList(Long familyId, List<FamilyMember> members) {
        List<FamilyMemberResource> memberResources = members.stream()
                .map(FamilyMemberResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new FamilyMembersListResource(
                familyId,
                memberResources,
                memberResources.size(),
                memberResources.isEmpty()
        );
    }
}

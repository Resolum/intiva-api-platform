package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.FamilyMemberResource;

/**
 * Assembler that converts a FamilyMember aggregate into a FamilyMemberResource REST response.
 */
public class FamilyMemberResourceFromEntityAssembler {

    /**
     * Converts a FamilyMember entity into a FamilyMemberResource.
     *
     * @param entity the FamilyMember aggregate to convert
     * @return the corresponding FamilyMemberResource
     */
    public static FamilyMemberResource toResourceFromEntity(FamilyMember entity) {
        return new FamilyMemberResource(
                entity.getId(),
                entity.getUserId().getValue(),
                entity.getFamilyId(),
                entity.getRole().name(),
                entity.getStatus().name(),
                entity.getJoinedAt().toString()
        );
    }
}

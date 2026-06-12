package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.aggregates.Family;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.FamilyResource;

/**
 * Assembler that converts a Family aggregate into a FamilyResource REST response.
 */
public class FamilyResourceFromEntityAssembler {

    /**
     * Converts a Family entity into a FamilyResource.
     *
     * @param entity the Family aggregate to convert
     * @return the corresponding FamilyResource
     */
    public static FamilyResource toResourceFromEntity(Family entity) {
        return new FamilyResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStatus().name(),
                entity.getOwnerId().getValue(),
                entity.getResourcesUsage().membersActive(),
                entity.getCreatedAt().toString()
        );
    }
}

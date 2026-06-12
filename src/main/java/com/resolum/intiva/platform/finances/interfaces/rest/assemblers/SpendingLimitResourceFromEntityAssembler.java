package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.SpendingLimitResource;

/**
 * Maps a SpendingLimit aggregate into its REST response representation.
 */
public class SpendingLimitResourceFromEntityAssembler {

    public static SpendingLimitResource toResourceFromEntity(SpendingLimit entity) {
        return new SpendingLimitResource(
                entity.getId(),
                entity.getOwnerId(),
                entity.getOwnerType().name(),
                entity.getTargetType().name(),
                entity.getTargetId(),
                entity.getLimitAmount().amount().toString(),
                entity.getSpentAmount().amount().toString(),
                entity.getLimitAmount().getCurrencyCode(),
                entity.getStartDate().toString(),
                entity.getEndDate().toString(),
                entity.getActive(),
                entity.getStatus().name()
        );
    }
}

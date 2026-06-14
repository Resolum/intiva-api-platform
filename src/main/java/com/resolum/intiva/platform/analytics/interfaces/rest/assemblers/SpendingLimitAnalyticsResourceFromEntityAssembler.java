package com.resolum.intiva.platform.analytics.interfaces.rest.assemblers;

import com.resolum.intiva.platform.analytics.domain.model.aggregates.SpendingLimitAnalytics;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.SpendingLimitAnalyticsResource;

/**
 * Maps a {@link SpendingLimitAnalytics} aggregate into its REST response representation.
 */
public class SpendingLimitAnalyticsResourceFromEntityAssembler {

    /**
     * Converts a spending limit analytics aggregate into an API response resource.
     *
     * @param entity the spending limit analytics aggregate
     * @return the corresponding response resource
     */
    public static SpendingLimitAnalyticsResource toResourceFromEntity(SpendingLimitAnalytics entity) {
        return new SpendingLimitAnalyticsResource(
                entity.getOwnerId(),
                entity.getOwnerType().name(),
                entity.getTotalLimitsSet(),
                entity.getLimitsExceeded(),
                entity.getLimitsAtWarning(),
                entity.getLimitsSafe(),
                entity.exceededRate(),
                entity.warningRate(),
                entity.getDetails().stream()
                        .map(SpendingLimitDetailResourceFromEntityAssembler::toResourceFromEntity)
                        .toList(),
                entity.getGeneratedAt()
        );
    }
}

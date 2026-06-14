package com.resolum.intiva.platform.analytics.interfaces.rest.assemblers;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.SpendingLimitDetail;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.MoneyResource;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.SpendingLimitDetailResource;

/**
 * Maps a {@link SpendingLimitDetail} value object into its REST response representation.
 */
public class SpendingLimitDetailResourceFromEntityAssembler {

    /**
     * Converts a spending limit detail value object into an API response resource.
     *
     * @param entity the spending limit detail value object
     * @return the corresponding response resource
     */
    public static SpendingLimitDetailResource toResourceFromEntity(SpendingLimitDetail entity) {
        return new SpendingLimitDetailResource(
                entity.spendingLimitId(),
                entity.categoryId(),
                entity.categoryName(),
                new MoneyResource(entity.limitAmount().getAmount(), entity.limitAmount().getCurrencyCode()),
                new MoneyResource(entity.currentAmount().getAmount(), entity.currentAmount().getCurrencyCode()),
                entity.usagePercentage(),
                entity.status().name()
        );
    }
}

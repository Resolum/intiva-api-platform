package com.resolum.intiva.platform.analytics.interfaces.rest.assemblers;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.AnalyticsPeriodTrend;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.AnalyticsPeriodTrendResource;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.MoneyResource;

/**
 * Maps an {@link AnalyticsPeriodTrend} value object into its REST response representation.
 */
public class AnalyticsPeriodTrendResourceFromEntityAssembler {

    /**
     * Converts an analytics period trend value object into an API response resource.
     *
     * @param entity the analytics period trend value object
     * @return the corresponding response resource
     */
    public static AnalyticsPeriodTrendResource toResourceFromEntity(AnalyticsPeriodTrend entity) {
        return new AnalyticsPeriodTrendResource(
                entity.period().start(),
                entity.period().end(),
                entity.period().periodType().name(),
                new MoneyResource(entity.totalIncome().getAmount(), entity.totalIncome().getCurrencyCode()),
                new MoneyResource(entity.totalExpenses().getAmount(), entity.totalExpenses().getCurrencyCode()),
                new MoneyResource(entity.netBalance().getAmount(), entity.netBalance().getCurrencyCode())
        );
    }
}

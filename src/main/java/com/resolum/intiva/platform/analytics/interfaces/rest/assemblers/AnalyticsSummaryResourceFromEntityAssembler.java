package com.resolum.intiva.platform.analytics.interfaces.rest.assemblers;

import com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsSummary;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.AnalyticsSummaryResource;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.MoneyResource;

/**
 * Maps an {@link AnalyticsSummary} aggregate into its REST response representation.
 */
public class AnalyticsSummaryResourceFromEntityAssembler {

    /**
     * Converts an analytics summary aggregate into an API response resource.
     *
     * @param entity the analytics summary aggregate
     * @return the corresponding response resource
     */
    public static AnalyticsSummaryResource toResourceFromEntity(AnalyticsSummary entity) {
        return new AnalyticsSummaryResource(
                entity.getOwnerId(),
                entity.getOwnerType().name(),
                entity.getPeriodType().name(),
                entity.getPeriodStart(),
                entity.getPeriodEnd(),
                new MoneyResource(entity.getTotalIncome().getAmount(), entity.getTotalIncome().getCurrencyCode()),
                new MoneyResource(entity.getTotalExpenses().getAmount(), entity.getTotalExpenses().getCurrencyCode()),
                new MoneyResource(entity.getNetBalance().getAmount(), entity.getNetBalance().getCurrencyCode()),
                entity.savingsRate(),
                entity.getExpensesByCategory().stream()
                        .map(CategoryExpenseSummaryResourceFromEntityAssembler::toResourceFromEntity)
                        .toList(),
                entity.getGeneratedAt()
        );
    }
}

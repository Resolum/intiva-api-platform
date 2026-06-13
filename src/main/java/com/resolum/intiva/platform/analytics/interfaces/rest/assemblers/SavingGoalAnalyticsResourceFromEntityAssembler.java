package com.resolum.intiva.platform.analytics.interfaces.rest.assemblers;

import com.resolum.intiva.platform.analytics.domain.model.aggregates.SavingGoalAnalytics;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.MoneyResource;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.SavingGoalAnalyticsResource;

/**
 * Maps a {@link SavingGoalAnalytics} aggregate into its REST response representation.
 */
public class SavingGoalAnalyticsResourceFromEntityAssembler {

    /**
     * Converts a saving goal analytics aggregate into an API response resource.
     *
     * @param entity the saving goal analytics aggregate
     * @return the corresponding response resource
     */
    public static SavingGoalAnalyticsResource toResourceFromEntity(SavingGoalAnalytics entity) {
        var totalTargetAmount = entity.getTotalTargetAmount() != null
                ? new MoneyResource(entity.getTotalTargetAmount().getAmount(), entity.getTotalTargetAmount().getCurrencyCode())
                : new MoneyResource(java.math.BigDecimal.ZERO, "PEN");
        var totalCurrentAmount = entity.getTotalCurrentAmount() != null
                ? new MoneyResource(entity.getTotalCurrentAmount().getAmount(), entity.getTotalCurrentAmount().getCurrencyCode())
                : new MoneyResource(java.math.BigDecimal.ZERO, "PEN");
        return new SavingGoalAnalyticsResource(
                entity.getOwnerId(),
                entity.getOwnerType().name(),
                entity.getTotalGoals(),
                entity.getGoalsCompleted(),
                entity.getGoalsInProgress(),
                entity.getGoalsUncompleted(),
                totalTargetAmount,
                totalCurrentAmount,
                entity.getOverallProgress(),
                entity.completionRate(),
                entity.getDetails().stream()
                        .map(SavingGoalDetailResourceFromEntityAssembler::toResourceFromEntity)
                        .toList(),
                entity.getGeneratedAt()
        );
    }
}

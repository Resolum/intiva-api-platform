package com.resolum.intiva.platform.analytics.interfaces.rest.assemblers;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.SavingGoalDetail;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.MoneyResource;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses.SavingGoalDetailResource;

/**
 * Maps a {@link SavingGoalDetail} value object into its REST response representation.
 */
public class SavingGoalDetailResourceFromEntityAssembler {

    /**
     * Converts a saving goal detail value object into an API response resource.
     *
     * @param entity the saving goal detail value object
     * @return the corresponding response resource
     */
    public static SavingGoalDetailResource toResourceFromEntity(SavingGoalDetail entity) {
        return new SavingGoalDetailResource(
                entity.savingGoalId(),
                entity.title(),
                new MoneyResource(entity.targetAmount().getAmount(), entity.targetAmount().getCurrencyCode()),
                new MoneyResource(entity.currentAmount().getAmount(), entity.currentAmount().getCurrencyCode()),
                entity.progressPercentage(),
                entity.deadline(),
                entity.status().name(),
                entity.daysRemaining()
        );
    }
}

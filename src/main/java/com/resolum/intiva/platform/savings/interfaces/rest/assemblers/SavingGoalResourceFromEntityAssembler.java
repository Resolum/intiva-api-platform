package com.resolum.intiva.platform.savings.interfaces.rest.assemblers;

import com.resolum.intiva.platform.savings.domain.model.aggregates.SavingGoal;
import com.resolum.intiva.platform.savings.interfaces.rest.resources.responses.SavingGoalResource;
import java.time.temporal.ChronoUnit;
import java.time.Instant;

/**
 * Assembler to convert a SavingGoal entity into a SavingGoalResource for REST responses.
 */
public class SavingGoalResourceFromEntityAssembler {
    
    /**
     * Converts a SavingGoal entity into a SavingGoalResource.
     * Calculates the remaining days dynamically based on the deadline.
     *
     * @param entity the SavingGoal entity to convert
     * @return the corresponding SavingGoalResource
     */
    public static SavingGoalResource toResourceFromEntity(SavingGoal entity) {
        return new SavingGoalResource(
                entity.getId(),
                entity.getOwnerType().name(),
                entity.getActorUserId(),
                entity.getOwnerId(),
                entity.getTitle(),
                entity.getCurrentAmount().amount(),
                entity.getTargetAmount().amount(),
                entity.getCurrentAmount().currencyCode().name(),
                entity.getDescription(),
                entity.getStartsAt(),
                entity.getDeadline(),
                entity.getDeadline() != null ? ChronoUnit.DAYS.between(Instant.now(), entity.getDeadline()) : null,
                entity.getStatus().name(),
                entity.getCategoryId(),
                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }
}

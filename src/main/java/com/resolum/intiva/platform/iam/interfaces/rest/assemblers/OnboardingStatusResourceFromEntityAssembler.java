package com.resolum.intiva.platform.iam.interfaces.rest.assemblers;

import com.resolum.intiva.platform.iam.domain.model.aggregates.Onboarding;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.responses.OnboardingStatusResource;

import java.time.ZoneId;

public class OnboardingStatusResourceFromEntityAssembler {

    public static OnboardingStatusResource toResourceFromEntity(Onboarding entity) {

        String completedAt = entity.getCompletedAt() != null
                ? entity.getCompletedAt()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .toString()
                : null;

        return new OnboardingStatusResource(
                entity.getId(),
                entity.getUserId(),
                entity.getCurrentStep().toString(),
                entity.isOnboardingCompleted(),
                completedAt
        );
    }
}

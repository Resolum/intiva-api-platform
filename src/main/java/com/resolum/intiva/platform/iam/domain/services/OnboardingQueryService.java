package com.resolum.intiva.platform.iam.domain.services;

import com.resolum.intiva.platform.iam.domain.model.aggregates.Onboarding;
import com.resolum.intiva.platform.iam.domain.model.queries.GetOnboardingStatusQuery;

import java.util.Optional;

/**
 * Service interface for handling onboarding-related queries.
 */
public interface OnboardingQueryService {

    /**
     * Handles the GetOnboardingStatus query to retrieve the onboarding status for a user.
     * @param query the GetOnboardingStatus query containing the user ID
     * @return an Optional containing the Onboarding status if found, or empty if not found
     */
    Optional<Onboarding> handle(GetOnboardingStatusQuery query);
}

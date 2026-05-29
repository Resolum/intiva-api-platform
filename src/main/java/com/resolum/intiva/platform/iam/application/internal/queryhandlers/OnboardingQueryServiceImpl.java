package com.resolum.intiva.platform.iam.application.internal.queryhandlers;

import com.resolum.intiva.platform.iam.domain.model.aggregates.Onboarding;
import com.resolum.intiva.platform.iam.domain.model.queries.GetOnboardingStatusQuery;
import com.resolum.intiva.platform.iam.domain.services.OnboardingQueryService;
import com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories.OnboardingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the OnboardingQueryService interface that handles queries related to onboarding status.
 */
@Service
public class OnboardingQueryServiceImpl implements OnboardingQueryService {

    /** Repository for accessing onboarding data. */
    private final OnboardingRepository onboardingRepository;

    /**
     * Constructor for OnboardingQueryServiceImpl.
     * @param onboardingRepository the repository for accessing onboarding data
     */
    public OnboardingQueryServiceImpl(OnboardingRepository onboardingRepository) {
        this.onboardingRepository = onboardingRepository;
    }

    /**
     * Handles the GetOnboardingStatus query by retrieving the onboarding status for a given user ID.
     * @param query the GetOnboardingStatus query containing the user ID
     * @return an Optional containing the Onboarding status if found, or empty if not found
     */
    @Override
    public Optional<Onboarding> handle(GetOnboardingStatusQuery query) {
        return onboardingRepository.findById(query.userId());
    }
}

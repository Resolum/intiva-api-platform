package com.resolum.intiva.platform.profiles.domain.model.queries;

/**
 * Query to get the onboarding status of a user.
 */
public record GetOnboardingStatusQuery(
        Long userId
) {
}

package com.resolum.intiva.platform.iam.interfaces.rest.resources.responses;

/**
 * Resource representing the onboarding status of a user.
 */
public record OnboardingStatusResource(
        Long onboardingId,
        Long userId,
        String currentStep,
        boolean onboardingCompleted,
        String completedAt
) {
}

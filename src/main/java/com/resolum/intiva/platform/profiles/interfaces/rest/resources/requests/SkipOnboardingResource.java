package com.resolum.intiva.platform.profiles.interfaces.rest.resources.requests;

/**
 * Resource containing the user whose onboarding tutorial should be skipped.
 *
 * @param userId the ID of the user
 */
public record SkipOnboardingResource(
        Long userId
) {
}

package com.resolum.intiva.platform.iam.interfaces.rest.resources.requests;

/**
 * RollbackOnboardingResource is a record that represents the request body for rolling back a user's onboarding process.
 * It contains the user ID of the user whose onboarding process is to be rolled back.
 */
public record RollbackOnboardingResource(
        Long userId
) {
}

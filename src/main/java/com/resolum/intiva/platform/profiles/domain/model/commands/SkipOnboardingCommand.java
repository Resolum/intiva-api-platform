package com.resolum.intiva.platform.profiles.domain.model.commands;

/**
 * Command to skip the onboarding tutorial for a user.
 *
 * @param userId the id of the user skipping onboarding
 */
public record SkipOnboardingCommand(
        Long userId
) {
}

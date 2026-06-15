package com.resolum.intiva.platform.profiles.domain.model.commands;

/**
 * Command to roll back the onboarding process for a user.
 *
 * @param userId the id of the user for which the onboarding is being rolled back
 */
public record RollbackOnboardingCommand(
        Long userId
) {
}

package com.resolum.intiva.platform.profiles.domain.model.commands;

/**
 * Command to create a user onboarding for a user.
 *
 * @param userId the id of the user for which the onboarding is being created
 */
public record CreateUserOnboardingCommand(
        Long userId
) {
}

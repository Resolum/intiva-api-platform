package com.resolum.intiva.platform.profiles.domain.model.commands;

/**
 * UpdateProfileCommand is a command object that encapsulates the data required to update
 * an existing user profile in the profiles bounded context.
 *
 * <p>This command is used by the application layer to propagate profile field changes
 * such as display name, biography, phone number, and age.</p>
 *
 * @param userId     the identifier of the user whose profile is being updated
 * @param name       the new display name for the profile
 * @param bio        the new biography text for the profile
 * @param phoneNumber the new phone number for the profile
 * @param age        the new age value for the profile
 */
public record UpdateProfileCommand(
        Long userId,
        String name,
        String bio,
        String phoneNumber,
        Integer age
) {
}

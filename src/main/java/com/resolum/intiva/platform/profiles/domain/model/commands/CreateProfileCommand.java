package com.resolum.intiva.platform.profiles.domain.model.commands;

/**
 * Command to create a new profile for a registered user.
 * Triggered automatically when a UserRegisteredEvent is received.
 *
 * @param userId the IAM user identifier
 * @param name   display name derived from the email local part
 */
public record CreateProfileCommand(Long userId, String name) {
}

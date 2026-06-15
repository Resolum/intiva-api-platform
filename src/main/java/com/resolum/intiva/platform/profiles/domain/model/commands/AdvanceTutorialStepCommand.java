package com.resolum.intiva.platform.profiles.domain.model.commands;

/**
 * Command to advance the user to the next step of the tutorial.
 *
 * @param userId the id of the user to advance
 */
public record AdvanceTutorialStepCommand(
        Long userId
) {
}

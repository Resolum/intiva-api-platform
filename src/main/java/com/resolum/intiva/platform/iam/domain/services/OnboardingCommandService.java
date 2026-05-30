package com.resolum.intiva.platform.iam.domain.services;

import com.resolum.intiva.platform.iam.domain.model.commands.AdvanceTutorialStepCommand;
import com.resolum.intiva.platform.iam.domain.model.commands.CreateUserOnboardingCommand;
import com.resolum.intiva.platform.iam.domain.model.commands.RollbackOnboardingCommand;

/**
 * Service interface for handling onboarding-related commands.
 */
public interface OnboardingCommandService {

    /**
     * Handles the command to create a user onboarding process.
     *
     * @param command the command containing the necessary information to create the onboarding process
     */
    void handle(CreateUserOnboardingCommand command);

    /**
     * Handles the command to advance the tutorial step in the onboarding process.
     *
     * @param command the command containing the necessary information to advance the tutorial step
     */
    void handle(AdvanceTutorialStepCommand command);

    /**
     * Handles the command to rollback the onboarding process.
     *
     * @param command the command containing the necessary information to rollback the onboarding process
     */
    void handle(RollbackOnboardingCommand command);
}

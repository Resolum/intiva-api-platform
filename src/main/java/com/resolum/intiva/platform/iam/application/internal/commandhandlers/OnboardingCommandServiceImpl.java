package com.resolum.intiva.platform.iam.application.internal.commandhandlers;

import com.resolum.intiva.platform.iam.domain.model.aggregates.Onboarding;
import com.resolum.intiva.platform.iam.domain.model.commands.AdvanceTutorialStepCommand;
import com.resolum.intiva.platform.iam.domain.model.commands.CreateUserOnboardingCommand;
import com.resolum.intiva.platform.iam.domain.model.commands.RollbackOnboardingCommand;
import com.resolum.intiva.platform.iam.domain.services.OnboardingCommandService;
import com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories.OnboardingRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of the OnboardingCommandService interface that handles commands related to user onboarding.
 */
@Service
public class OnboardingCommandServiceImpl implements OnboardingCommandService {

    private final OnboardingRepository onboardingRepository;

    /**
     * Constructor for OnboardingCommandServiceImpl.
     *
     * @param onboardingRepository the repository for accessing onboarding data
     */
    public OnboardingCommandServiceImpl(OnboardingRepository onboardingRepository) {
        this.onboardingRepository = onboardingRepository;
    }

    /**
     * Handles the CreateUserOnboardingCommand by creating a new onboarding instance for the user and saving it to the repository.
     *
     * @param command the command containing the user ID for which to create the onboarding
     */
    @Override
    public void handle(CreateUserOnboardingCommand command) {
        var onboarding = new Onboarding(command.userId());
        onboardingRepository.save(onboarding);
    }

    /**
     * Handles the AdvanceTutorialStepCommand by retrieving the onboarding for the user, advancing the tutorial step, and saving the updated onboarding back to the repository.
     *
     * @param command the command containing the user ID for which to advance the tutorial step
     */
    @Override
    public void handle(AdvanceTutorialStepCommand command) {
        var onboarding = onboardingRepository.findByUserId(command.userId()).orElseThrow();
        onboarding.advanceTutorialStep();
        onboardingRepository.save(onboarding);
    }

    /**
     * Handles the RollbackOnboardingCommand by retrieving the onboarding for the user, rolling back the tutorial step, and saving the updated onboarding back to the repository.
     *
     * @param command the command containing the user ID for which to rollback the tutorial step
     */
    @Override
    public void handle(RollbackOnboardingCommand command) {
        var onboarding = onboardingRepository.findByUserId(command.userId()).orElseThrow();
        onboarding.rollbackTutorialStep();
        onboardingRepository.save(onboarding);
    }
}

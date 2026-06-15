package com.resolum.intiva.platform.profiles.application.internal.commandservices;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Onboarding;
import com.resolum.intiva.platform.profiles.domain.model.commands.AdvanceTutorialStepCommand;
import com.resolum.intiva.platform.profiles.domain.model.commands.CreateUserOnboardingCommand;
import com.resolum.intiva.platform.profiles.domain.model.commands.RollbackOnboardingCommand;
import com.resolum.intiva.platform.profiles.domain.model.commands.SkipOnboardingCommand;
import com.resolum.intiva.platform.profiles.domain.model.services.OnboardingCommandService;
import com.resolum.intiva.platform.profiles.infrastructure.persistence.jpa.repositories.OnboardingRepository;
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

    /**
     * Handles the SkipOnboardingCommand by marking onboarding as completed.
     *
     * @param command the command containing the user ID whose onboarding should be skipped
     */
    @Override
    public void handle(SkipOnboardingCommand command) {
        var onboarding = onboardingRepository.findByUserId(command.userId()).orElseThrow();
        onboarding.skipTutorial();
        onboardingRepository.save(onboarding);
    }
}

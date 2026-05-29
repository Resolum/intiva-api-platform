package com.resolum.intiva.platform.iam.domain.services;

import com.resolum.intiva.platform.iam.domain.model.commands.AdvanceTutorialStepCommand;
import com.resolum.intiva.platform.iam.domain.model.commands.CreateUserOnboardingCommand;

public interface OnboardingCommandService {

    void handle(CreateUserOnboardingCommand command);

    void handle(AdvanceTutorialStepCommand command);
}

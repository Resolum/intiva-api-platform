package com.resolum.intiva.platform.profiles.interfaces.rest.assemblers;

import com.resolum.intiva.platform.profiles.domain.model.commands.AdvanceTutorialStepCommand;
import com.resolum.intiva.platform.profiles.interfaces.rest.resources.requests.AdvanceOnboardingProcessResource;

/**
 * Assembler class to convert AdvanceOnboardingProcessResource to AdvanceTutorialStepCommand.
 */
public class AdvanceTutorialStepCommandFromResourceAssembler {

    /**
     * Static method to convert AdvanceOnboardingProcessResource to AdvanceTutorialStepCommand
     * @param resource the AdvanceOnboardingProcessResource to convert
     * @return the converted AdvanceTutorialStepCommand
     */
    public static AdvanceTutorialStepCommand toCommandFromResource(AdvanceOnboardingProcessResource resource) {
        return new AdvanceTutorialStepCommand(
                resource.userId()
        );
    }
}

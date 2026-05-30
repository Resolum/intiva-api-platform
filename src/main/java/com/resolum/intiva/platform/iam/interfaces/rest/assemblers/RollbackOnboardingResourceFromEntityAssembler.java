package com.resolum.intiva.platform.iam.interfaces.rest.assemblers;

import com.resolum.intiva.platform.iam.domain.model.commands.RollbackOnboardingCommand;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.requests.RollbackOnboardingResource;

/**
 * Assembler class to convert RollbackOnboardingResource to RollbackOnboardingCommand.
 */
public class RollbackOnboardingResourceFromEntityAssembler {

    /**
     * Static method to convert RollbackOnboardingResource to RollbackOnboardingCommand
     * @param resource the RollbackOnboardingResource to convert
     * @return the converted RollbackOnboardingCommand
     */
    public static RollbackOnboardingCommand toCommandFromResource(RollbackOnboardingResource resource) {
        return new RollbackOnboardingCommand(resource.userId());
    }
}

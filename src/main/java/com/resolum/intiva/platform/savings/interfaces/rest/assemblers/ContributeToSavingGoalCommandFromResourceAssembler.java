package com.resolum.intiva.platform.savings.interfaces.rest.assemblers;

import com.resolum.intiva.platform.savings.domain.model.commands.ContributeToSavingGoalCommand;
import com.resolum.intiva.platform.savings.interfaces.rest.resources.requests.ContributeToSavingGoalResource;

/**
 * Assembler to create a ContributeToSavingGoalCommand from a REST resource request.
 */
public class ContributeToSavingGoalCommandFromResourceAssembler {
    
    /**
     * Converts a ContributeToSavingGoalResource into a ContributeToSavingGoalCommand.
     *
     * @param savingGoalId the ID of the saving goal receiving the contribution
     * @param resource     the resource containing contribution details
     * @return the corresponding ContributeToSavingGoalCommand
     */
    public static ContributeToSavingGoalCommand toCommandFromResource(Long savingGoalId, ContributeToSavingGoalResource resource) {
        return new ContributeToSavingGoalCommand(
                savingGoalId,
                resource.amount(),
                resource.currencyCode(),
                resource.contributorId()
        );
    }
}

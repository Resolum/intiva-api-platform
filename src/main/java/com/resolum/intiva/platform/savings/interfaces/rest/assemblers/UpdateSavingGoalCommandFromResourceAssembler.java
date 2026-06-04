package com.resolum.intiva.platform.savings.interfaces.rest.assemblers;

import com.resolum.intiva.platform.savings.domain.model.commands.UpdateSavingGoalCommand;
import com.resolum.intiva.platform.savings.interfaces.rest.resources.requests.UpdateSavingGoalResource;

/**
 * Assembler to create an UpdateSavingGoalCommand from a REST resource request.
 */
public class UpdateSavingGoalCommandFromResourceAssembler {

    /**
     * Converts an UpdateSavingGoalResource into an UpdateSavingGoalCommand.
     *
     * @param savingGoalId the ID of the saving goal to update, taken from the path variable
     * @param resource     the resource containing the fields to update
     * @return the corresponding UpdateSavingGoalCommand
     */
    public static UpdateSavingGoalCommand toCommandFromResource(Long savingGoalId, UpdateSavingGoalResource resource) {
        return new UpdateSavingGoalCommand(
                savingGoalId,
                resource.title(),
                resource.description(),
                resource.newTargetAmount()
        );
    }
}

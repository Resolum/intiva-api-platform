package com.resolum.intiva.platform.savings.interfaces.rest.assemblers;

import com.resolum.intiva.platform.savings.domain.model.commands.CreateSavingGoalCommand;
import com.resolum.intiva.platform.savings.interfaces.rest.resources.requests.CreateSavingGoalResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

/**
 * Assembler to create a CreateSavingGoalCommand from a REST resource request.
 */
public class CreateSavingGoalCommandFromResourceAssembler {
    
    /**
     * Converts a CreateSavingGoalResource into a CreateSavingGoalCommand.
     * Maps the owner type and currency code strings to their corresponding enums.
     *
     * @param resource the resource containing the new saving goal details
     * @return the corresponding CreateSavingGoalCommand
     */
    public static CreateSavingGoalCommand toCommandFromResource(CreateSavingGoalResource resource) {
        String type = resource.ownerType().toUpperCase();
        OwnerTypes parsedType = type.equals("FAMILY") ? OwnerTypes.FAMILY : OwnerTypes.INDIVIDUAL;

        return new CreateSavingGoalCommand(
                parsedType,
                resource.actorUserId(),
                resource.ownerId(),
                resource.title(),
                resource.targetAmount(),
                CurrencyCodes.fromString(resource.currencyCode().toUpperCase()),
                resource.description(),
                resource.startsAt(),
                resource.deadline(),
                resource.categoryId()
        );
    }
}

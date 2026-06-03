package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.commands.CreateFamilyCommand;
import com.resolum.intiva.platform.household.interfaces.rest.resources.requests.CreateFamilyResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Assembler that creates a CreateFamilyCommand from a REST resource and the authenticated owner.
 */
public class CreateFamilyCommandFromResourceAssembler {

    /**
     * Converts a CreateFamilyResource and owner identifier into a CreateFamilyCommand.
     *
     * @param resource the request body with family group details
     * @param ownerId  the numeric user ID of the authenticated owner (from Principal)
     * @return the corresponding CreateFamilyCommand
     */
    public static CreateFamilyCommand toCommandFromResource(CreateFamilyResource resource, Long ownerId) {
        return new CreateFamilyCommand(
                resource.name(),
                resource.description(),
                new UserId(ownerId)
        );
    }
}

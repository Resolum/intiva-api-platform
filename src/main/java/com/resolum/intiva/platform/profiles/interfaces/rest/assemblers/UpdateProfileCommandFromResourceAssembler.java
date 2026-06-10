package com.resolum.intiva.platform.profiles.interfaces.rest.assemblers;

import com.resolum.intiva.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.resolum.intiva.platform.profiles.interfaces.rest.resources.requests.UpdateProfileResource;

/**
 * Assembler that converts an incoming REST API request (UpdateProfileResource) into
 * an UpdateProfileCommand for processing in the application layer.
 */
public class UpdateProfileCommandFromResourceAssembler {
    /**
     * Creates an UpdateProfileCommand from the authenticated user id and the request payload.
     *
     * @param userId   the identifier of the authenticated user making the update
     * @param resource the request body containing the profile fields to update
     * @return a populated UpdateProfileCommand ready for handling
     */
    public static UpdateProfileCommand toCommandFromResource(Long userId, UpdateProfileResource resource) {
        return new UpdateProfileCommand(
                userId,
                resource.name(),
                resource.bio(),
                resource.phoneNumber(),
                resource.age()
        );
    }
}

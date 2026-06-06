package com.resolum.intiva.platform.iam.interfaces.rest.assemblers;

import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.responses.AuthenticatedUserResource;

/**
 * Assembler class responsible for converting authenticated user entities to UserResource DTOs.
 */
public class AuthenticatedUserResourceFromEntityAssembler {

    /**
     * Converts a User entity to a UserResource DTO.
     * @param user the user to convert
     * @param token the JWT token
     * @return the converted UserResource
     */
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        return new AuthenticatedUserResource(user.getId(), user.getEmail().getValue(), token);
    }
}

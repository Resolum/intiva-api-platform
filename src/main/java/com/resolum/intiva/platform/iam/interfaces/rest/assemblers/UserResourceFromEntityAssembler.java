package com.resolum.intiva.platform.iam.interfaces.rest.assemblers;

import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.responses.UserResource;

/**
 * Assembler class responsible for converting User entities to UserResource DTOs.
 */
public class UserResourceFromEntityAssembler {

    /**
     * Converts a User entity to a UserResource DTO.
     * @param user The User entity to convert.
     * @return A UserResource DTO containing the user's information.
     */
    public static UserResource toResourceFromEntity(User user) {
        return new UserResource(
                user.getId(),
                user.getEmail().getValue()
        );
    }
}

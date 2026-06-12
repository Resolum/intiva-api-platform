package com.resolum.intiva.platform.profiles.interfaces.rest.assemblers;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Profile;
import com.resolum.intiva.platform.profiles.interfaces.rest.resources.responses.ProfileResource;

/**
 * Assembler that converts a Profile domain entity into a ProfileResource representation
 * for REST API responses.
 */
public class ProfileResourceFromEntityAssembler {
    /**
     * Converts a Profile entity and its associated email into a ProfileResource DTO.
     *
     * @param entity the Profile aggregate to convert
     * @param email  the user's email address obtained from the IAM context
     * @return a fully populated ProfileResource ready for serialization
     */
    public static ProfileResource toResourceFromEntity(Profile entity, String email) {
        return new ProfileResource(
                entity.getId(),
                entity.getUserId().userId(),
                entity.getName(),
                entity.getAge(),
                entity.getAvatarUrl() != null ? entity.getAvatarUrl().getUrl() : null,
                entity.getPhoneNumber(),
                entity.getBio(),
                email
        );
    }
}

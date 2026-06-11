package com.resolum.intiva.platform.profiles.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * UpdateProfileResource is a record that represents the data structure received through the REST API
 * when updating a user's personal profile information.
 *
 * @param name The display name of the profile owner. This field is mandatory and cannot be blank.
 * @param bio A short biography or description of the profile owner.
 * @param phoneNumber The contact phone number of the profile owner.
 */
@Schema(description = "Request body used to update profile personal information.")
public record UpdateProfileResource(
        @NotBlank
        @Schema(description = "Display name of the profile owner.", example = "John Doe")
        String name,

        @Schema(description = "Short biography of the profile owner.", example = "Software Engineer & Tech Enthusiast")
        String bio,

        @Schema(description = "Contact phone number of the profile owner.", example = "+51987654321")
        String phoneNumber,

        @Schema(description = "Age of the profile owner.", example = "25")
        Integer age
) {
}

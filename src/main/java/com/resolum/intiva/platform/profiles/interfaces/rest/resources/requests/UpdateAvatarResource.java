package com.resolum.intiva.platform.profiles.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

/**
 * UpdateAvatarResource is a record that represents the data structure received through the REST API
 * when updating a user's profile avatar using multipart/form-data.
 *
 * @param file The image file to be uploaded as the new profile avatar.
 */
@Schema(description = "Multipart request body used to upload a new profile avatar.")
public record UpdateAvatarResource(
        @Schema(description = "Avatar image file (JPEG, PNG).", type = "string", format = "binary")
        MultipartFile file
) {
}

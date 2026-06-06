package com.resolum.intiva.platform.communications.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST response that exposes one registered push-notification device token.
 *
 * @param id registered device identifier
 * @param userId owner of the token
 * @param deviceToken Firebase token
 * @param platform client platform
 * @param active whether the token is active
 * @param createdAt creation timestamp
 * @param updatedAt last update timestamp
 */
@Schema(description = "Registered mobile device token returned by the communications API.")
public record NotificationDeviceResource(
        Long id,
        Long userId,
        String deviceToken,
        String platform,
        Boolean active,
        String createdAt,
        String updatedAt
) {
}

package com.resolum.intiva.platform.communications.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body used by mobile clients to register one device token for push notifications.
 *
 * @param userId owner of the token being registered
 * @param deviceToken Firebase token returned by the mobile client SDK
 * @param platform client platform such as ANDROID or IOS
 */
@Schema(description = "Request body used to register one mobile device token for push notifications.")
public record RegisterNotificationDeviceResource(
        @Schema(description = "Authenticated user that owns the device token.", example = "7")
        Long userId,
        @Schema(description = "Firebase Cloud Messaging device token returned by the mobile SDK.", example = "fcm-device-token-123")
        String deviceToken,
        @Schema(description = "Client platform of the device token.", example = "ANDROID")
        String platform
) {
}

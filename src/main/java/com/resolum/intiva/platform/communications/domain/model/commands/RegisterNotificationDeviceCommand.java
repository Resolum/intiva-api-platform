package com.resolum.intiva.platform.communications.domain.model.commands;

/**
 * Command used to register or reactivate a device token for push notifications.
 *
 * @param userId owner of the device token
 * @param deviceToken Firebase token reported by the mobile client
 * @param platform client platform, such as ANDROID or IOS
 */
public record RegisterNotificationDeviceCommand(
        Long userId,
        String deviceToken,
        String platform
) {
}

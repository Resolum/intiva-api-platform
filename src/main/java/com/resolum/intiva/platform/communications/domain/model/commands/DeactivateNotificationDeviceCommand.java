package com.resolum.intiva.platform.communications.domain.model.commands;

/**
 * Command used to deactivate one registered device token.
 *
 * @param notificationDeviceId registered device identifier
 */
public record DeactivateNotificationDeviceCommand(Long notificationDeviceId) {
}

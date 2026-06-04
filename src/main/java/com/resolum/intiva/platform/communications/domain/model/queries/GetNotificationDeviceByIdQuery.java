package com.resolum.intiva.platform.communications.domain.model.queries;

/**
 * Query used to retrieve one registered notification device by identifier.
 *
 * @param notificationDeviceId registered device identifier
 */
public record GetNotificationDeviceByIdQuery(Long notificationDeviceId) {
}

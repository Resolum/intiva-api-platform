package com.resolum.intiva.platform.communications.domain.model.queries;

/**
 * Query used to retrieve one notification by its identifier.
 *
 * @param notificationId notification identifier
 */
public record GetNotificationByIdQuery(Long notificationId) {
}

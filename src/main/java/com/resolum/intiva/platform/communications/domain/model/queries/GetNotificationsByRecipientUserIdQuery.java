package com.resolum.intiva.platform.communications.domain.model.queries;

/**
 * Query used to retrieve all notifications for one recipient user.
 *
 * @param recipientUserId recipient user identifier
 */
public record GetNotificationsByRecipientUserIdQuery(Long recipientUserId) {
}

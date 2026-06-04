package com.resolum.intiva.platform.communications.domain.model.queries;

/**
 * Query used to retrieve unread notifications for one recipient user.
 *
 * @param recipientUserId recipient user identifier
 */
public record GetUnreadNotificationsByRecipientUserIdQuery(Long recipientUserId) {
}

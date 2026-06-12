package com.resolum.intiva.platform.communications.domain.model.commands;

/**
 * Command used to mark one persisted notification as read.
 *
 * @param notificationId notification identifier
 */
public record MarkNotificationAsReadCommand(Long notificationId) {
}

package com.resolum.intiva.platform.communications.domain.model.commands;

/**
 * Command used to persist an in-app notification for a user.
 *
 * @param recipientUserId user who will see the notification in the application
 * @param type business type of the notification
 * @param source business source that originated the notification
 * @param sourceId identifier of the aggregate or entity that originated the notification
 * @param title title shown to the user
 * @param message notification body shown to the user
 */
public record CreateInAppNotificationCommand(
        Long recipientUserId,
        String type,
        String source,
        Long sourceId,
        String title,
        String message
) {
}

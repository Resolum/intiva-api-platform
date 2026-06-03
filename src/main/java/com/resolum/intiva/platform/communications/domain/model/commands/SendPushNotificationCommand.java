package com.resolum.intiva.platform.communications.domain.model.commands;

/**
 * Command used to send a push notification through an external delivery provider.
 *
 * @param recipientUserId user who should receive the push notification
 * @param deviceToken target device token used by the push provider
 * @param type business type of the notification
 * @param source business source that originated the notification
 * @param sourceId identifier of the aggregate or entity that originated the notification
 * @param title title shown in the push notification
 * @param message body shown in the push notification
 */
public record SendPushNotificationCommand(
        Long recipientUserId,
        String deviceToken,
        String type,
        String source,
        Long sourceId,
        String title,
        String message
) {
}

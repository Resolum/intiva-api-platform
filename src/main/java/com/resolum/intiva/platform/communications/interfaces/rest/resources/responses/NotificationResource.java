package com.resolum.intiva.platform.communications.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST response that exposes a persisted in-app notification.
 *
 * @param id notification identifier
 * @param recipientUserId recipient user identifier
 * @param type notification business type
 * @param source business source that originated the notification
 * @param sourceId identifier of the aggregate or entity that originated the notification
 * @param title notification title
 * @param message notification body
 * @param status notification read status
 * @param createdAt notification creation timestamp
 */
@Schema(description = "Persisted in-app notification returned by the communications API.")
public record NotificationResource(
        Long id,
        Long recipientUserId,
        String type,
        String source,
        Long sourceId,
        String title,
        String message,
        String status,
        String createdAt
) {
}

package com.resolum.intiva.platform.communications.interfaces.acl;

import java.util.List;

/**
 * ACL facade that exposes notification capabilities to other bounded contexts.
 */
public interface CommunicationsContextFacade {

    /**
     * Persists one in-app notification for a recipient user.
     *
     * @param recipientUserId user who will receive the notification
     * @param type notification business type
     * @param source business source that originated the notification
     * @param sourceId source aggregate identifier
     * @param title notification title
     * @param message notification body
     */
    void createInAppNotification(
            Long recipientUserId,
            String type,
            String source,
            Long sourceId,
            String title,
            String message
    );

    /**
     * Sends one push notification to every active device token registered by the recipient user.
     *
     * @param recipientUserId user who should receive the push notification
     * @param type notification business type
     * @param source business source that originated the notification
     * @param sourceId source aggregate identifier
     * @param title notification title
     * @param message notification body
     */
    void sendPushNotificationToUser(
            Long recipientUserId,
            String type,
            String source,
            Long sourceId,
            String title,
            String message
    );


}

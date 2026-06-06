package com.resolum.intiva.platform.communications.application.internal.outboundservices.fcm;

import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;

/**
 * Outbound gateway that abstracts the push delivery provider used by the communications context.
 */
public interface FirebaseMessagingGateway {

    /** Sends one push notification to the given device token through the external provider.
     *
     * @param recipientUserId user who should receive the push notification
     * @param deviceToken active device token registered by the recipient user
     * @param type notification business type
     * @param source business source that originated the notification
     * @param sourceId source aggregate identifier
     * @param title notification title
     * @param message notification body
     */
    void send(Long recipientUserId,
              String deviceToken,
              String type,
              String source,
              Long sourceId,
              String title,
              String message);
}

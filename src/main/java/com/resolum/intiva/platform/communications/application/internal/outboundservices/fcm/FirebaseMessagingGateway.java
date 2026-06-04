package com.resolum.intiva.platform.communications.application.internal.outboundservices.fcm;

import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;

/**
 * Outbound gateway that abstracts the push delivery provider used by the communications context.
 */
public interface FirebaseMessagingGateway {

    /**
     * Sends a push notification using the configured external provider.
     *
     * @param command push notification command
     */
    void send(SendPushNotificationCommand command);
}

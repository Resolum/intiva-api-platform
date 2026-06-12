package com.resolum.intiva.platform.finances.application.internal.outboundservices.acl;

import com.resolum.intiva.platform.communications.interfaces.acl.CommunicationsContextFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ACL service that lets the finances bounded context request in-app notifications from communications.
 */
@Service
public class FinancesExternalNotificationsService {

    /**
     * Facade that exposes the communications notification capabilities.
     */
    private final CommunicationsContextFacade communicationsContextFacade;

    /**
     * Creates the ACL service with the communications facade dependency.
     *
     * @param communicationsContextFacade communications context facade
     */
    public FinancesExternalNotificationsService(CommunicationsContextFacade communicationsContextFacade) {
        this.communicationsContextFacade = communicationsContextFacade;
    }

    /**
     * Requests creation of one persisted in-app notification.
     *
     * @param recipientUserId user who should receive the notification
     * @param type notification business type
     * @param source business source that originated the notification
     * @param sourceId source aggregate identifier
     * @param title notification title
     * @param message notification body
     */
    public void createInAppNotification(
            Long recipientUserId,
            String type,
            String source,
            Long sourceId,
            String title,
            String message
    ) {
        communicationsContextFacade.createInAppNotification(
                recipientUserId,
                type,
                source,
                sourceId,
                title,
                message
        );
    }

    /**
     * Requests push delivery to every active device token of the recipient user.
     *
     * @param recipientUserId user who should receive the push notification
     * @param type notification business type
     * @param source business source that originated the notification
     * @param sourceId source aggregate identifier
     * @param title notification title
     * @param message notification body
     */
    public void sendPushNotificationToUser(
            Long recipientUserId,
            String type,
            String source,
            Long sourceId,
            String title,
            String message
    ) {
        communicationsContextFacade.sendPushNotificationToUser(
                recipientUserId,
                type,
                source,
                sourceId,
                title,
                message
        );
    }
}

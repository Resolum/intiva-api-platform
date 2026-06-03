package com.resolum.intiva.platform.communications.application.acl.services;

import com.resolum.intiva.platform.communications.domain.model.commands.CreateInAppNotificationCommand;
import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;
import com.resolum.intiva.platform.communications.domain.services.NotificationCommandService;
import com.resolum.intiva.platform.communications.infrastructure.persistence.jpa.repositories.NotificationDeviceRepository;
import com.resolum.intiva.platform.communications.interfaces.acl.CommunicationsContextFacade;
import org.springframework.stereotype.Service;

/**
 * Default ACL facade implementation that lets external bounded contexts create in-app notifications.
 */
@Service
public class CommunicationsContextFacadeImpl implements CommunicationsContextFacade {

    /**
     * Notification command service used to persist in-app notifications.
     */
    private final NotificationCommandService notificationCommandService;

    /**
     * Repository used to resolve active device tokens per recipient user.
     */
    private final NotificationDeviceRepository notificationDeviceRepository;

    /**
     * Creates the ACL facade with the notification command service dependency.
     *
     * @param notificationCommandService in-app notification command service
     */
    public CommunicationsContextFacadeImpl(
            NotificationCommandService notificationCommandService,
            NotificationDeviceRepository notificationDeviceRepository
    ) {
        this.notificationCommandService = notificationCommandService;
        this.notificationDeviceRepository = notificationDeviceRepository;
    }

    /**
     * Creates one persisted in-app notification through the communications bounded context.
     */
    @Override
    public void createInAppNotification(
            Long recipientUserId,
            String type,
            String source,
            Long sourceId,
            String title,
            String message
    ) {
        notificationCommandService.handle(new CreateInAppNotificationCommand(
                recipientUserId,
                type,
                source,
                sourceId,
                title,
                message
        ));
    }

    /**
     * Sends one push notification to every active device token of the given user.
     */
    @Override
    public void sendPushNotificationToUser(
            Long recipientUserId,
            String type,
            String source,
            Long sourceId,
            String title,
            String message
    ) {
        var activeDevices = notificationDeviceRepository.findByUserIdAndActiveTrueOrderByUpdatedAtDesc(recipientUserId);

        activeDevices.forEach(device -> notificationCommandService.handle(new SendPushNotificationCommand(
                recipientUserId,
                device.getDeviceToken(),
                type,
                source,
                sourceId,
                title,
                message
        )));
    }
}

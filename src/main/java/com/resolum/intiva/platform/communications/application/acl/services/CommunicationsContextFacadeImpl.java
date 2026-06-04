package com.resolum.intiva.platform.communications.application.acl.services;

import com.resolum.intiva.platform.communications.domain.model.commands.CreateInAppNotificationCommand;
import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;
import com.resolum.intiva.platform.communications.domain.services.NotificationCommandService;
import com.resolum.intiva.platform.communications.interfaces.acl.CommunicationsContextFacade;
import com.resolum.intiva.platform.household.interfaces.acl.HouseholdContextFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default ACL facade implementation that lets external bounded contexts create in-app notifications.
 */
@Service
@Slf4j
public class CommunicationsContextFacadeImpl implements CommunicationsContextFacade {

    /**
     * Notification command service used to persist in-app notifications.
     */
    private final NotificationCommandService notificationCommandService;

    /**
     * Repository used to query family members for group notification targeting.
     */
    private final HouseholdContextFacade householdContextFacade;

    /**
     * Creates the ACL facade with the notification command service dependency.
     *
     * @param notificationCommandService in-app notification command service
     */
    public CommunicationsContextFacadeImpl(
            NotificationCommandService notificationCommandService,
            HouseholdContextFacade householdContextFacade
    ) {
        this.notificationCommandService = notificationCommandService;
        this.householdContextFacade = householdContextFacade;
    }

    /**
     * Creates one persisted in-app notification through the communications-bounded context.
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
        log.info("Creating in-app notification. recipientUserId={}, type={}, source={}, sourceId={}, title={}",
                recipientUserId, type, source, sourceId, title);
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

        notificationCommandService.handle(new SendPushNotificationCommand(
                recipientUserId,
                type,
                source,
                sourceId,
                title,
                message
        ));
    }

    /**
     * Retrieves the user IDs of all active members of a family group.
     */
    @Override
    public List<Long> getMemberUserIdsByFamilyId(Long familyId) {
        return householdContextFacade.getActiveFamilyMemberUserIds(familyId);
    }
}

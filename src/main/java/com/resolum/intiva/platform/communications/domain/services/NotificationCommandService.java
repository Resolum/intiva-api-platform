package com.resolum.intiva.platform.communications.domain.services;

import com.resolum.intiva.platform.communications.domain.model.aggregates.Notification;
import com.resolum.intiva.platform.communications.domain.model.commands.CreateInAppNotificationCommand;
import com.resolum.intiva.platform.communications.domain.model.commands.MarkNotificationAsReadCommand;
import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;

import java.util.Optional;

/**
 * Application-facing command contract for the communications bounded context.
 *
 * <p>The same command service coordinates the two supported notification actions:
 * persisting in-app notifications and sending push notifications through an external provider.</p>
 */
public interface NotificationCommandService {

    /**
     * Persists an in-app notification in the system.
     *
     * @param command in-app notification creation command
     * @return created notification when persistence succeeds
     */
    Optional<Notification> handle(CreateInAppNotificationCommand command);

    /**
     * Sends a push notification through the configured push gateway.
     *
     * @param command push notification command
     */
    void handle(SendPushNotificationCommand command);

    /**
     * Marks a persisted notification as read.
     *
     * @param command notification status change command
     * @return updated notification when the notification exists
     */
    Optional<Notification> handle(MarkNotificationAsReadCommand command);
}

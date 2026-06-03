package com.resolum.intiva.platform.communications.application.internal.commandservices;

import com.resolum.intiva.platform.communications.application.internal.outboundservices.fcm.FirebaseMessagingGateway;
import com.resolum.intiva.platform.communications.domain.model.aggregates.Notification;
import com.resolum.intiva.platform.communications.domain.model.commands.CreateInAppNotificationCommand;
import com.resolum.intiva.platform.communications.domain.model.commands.MarkNotificationAsReadCommand;
import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationSource;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationType;
import com.resolum.intiva.platform.communications.domain.services.NotificationCommandService;
import com.resolum.intiva.platform.communications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Default application service for notification commands.
 *
 * <p>The service coordinates both persisted in-app notifications and push delivery while keeping
 * their entry point unified for the rest of the system.</p>
 */
@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    /**
     * Repository used to persist in-app notifications and update their read status.
     */
    private final NotificationRepository notificationRepository;

    /**
     * Outbound push gateway used to deliver push notifications to the external provider.
     */
    private final FirebaseMessagingGateway firebaseMessagingGateway;

    /**
     * Creates the command service with persistence and push-delivery dependencies.
     *
     * @param notificationRepository notification repository
     * @param firebaseMessagingGateway push delivery gateway
     */
    public NotificationCommandServiceImpl(
            NotificationRepository notificationRepository,
            FirebaseMessagingGateway firebaseMessagingGateway
    ) {
        this.notificationRepository = notificationRepository;
        this.firebaseMessagingGateway = firebaseMessagingGateway;
    }

    /**
     * Creates and persists an in-app notification.
     *
     * @param command in-app notification creation command
     * @return created notification
     */
    @Override
    public Optional<Notification> handle(CreateInAppNotificationCommand command) {
        var notification = new Notification(
                command.recipientUserId(),
                NotificationType.valueOf(command.type().toUpperCase()),
                NotificationSource.valueOf(command.source().toUpperCase()),
                command.sourceId(),
                command.title(),
                command.message()
        );

        return Optional.of(notificationRepository.save(notification));
    }

    /**
     * Delegates push delivery to the configured push provider gateway.
     *
     * @param command push notification command
     */
    @Override
    public void handle(SendPushNotificationCommand command) {
        firebaseMessagingGateway.send(command);
    }

    /**
     * Marks one notification as read and persists the new status.
     *
     * @param command read-status command
     * @return updated notification when it exists
     */
    @Override
    public Optional<Notification> handle(MarkNotificationAsReadCommand command) {
        var notification = notificationRepository.findById(command.notificationId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification with ID " + command.notificationId() + " does not exist."
                ));

        notification.markAsRead();
        return Optional.of(notificationRepository.save(notification));
    }
}

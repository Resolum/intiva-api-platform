package com.resolum.intiva.platform.communications.application.internal.queryservices;

import com.resolum.intiva.platform.communications.domain.model.aggregates.Notification;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationByIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationsByRecipientUserIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetUnreadNotificationsByRecipientUserIdQuery;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationStatus;
import com.resolum.intiva.platform.communications.domain.services.NotificationQueryService;
import com.resolum.intiva.platform.communications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Default query implementation for persisted notifications.
 */
@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    /**
     * Repository used to retrieve persisted notifications.
     */
    private final NotificationRepository notificationRepository;

    /**
     * Creates the query service with its repository dependency.
     *
     * @param notificationRepository notification repository
     */
    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Retrieves a notification by id.
     *
     * @param query identifier query
     * @return notification if it exists
     */
    @Override
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(query.notificationId());
    }

    /**
     * Retrieves notifications of one recipient user.
     *
     * @param query recipient query
     * @return notifications of the given recipient
     */
    @Override
    public List<Notification> handle(GetNotificationsByRecipientUserIdQuery query) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(query.recipientUserId());
    }

    /**
     * Retrieves unread notifications of one recipient user.
     *
     * @param query recipient query
     * @return unread notifications of the given recipient
     */
    @Override
    public List<Notification> handle(GetUnreadNotificationsByRecipientUserIdQuery query) {
        return notificationRepository.findByRecipientUserIdAndStatusOrderByCreatedAtDesc(
                query.recipientUserId(),
                NotificationStatus.UNREAD
        );
    }
}

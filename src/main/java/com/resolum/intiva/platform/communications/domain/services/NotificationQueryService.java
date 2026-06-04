package com.resolum.intiva.platform.communications.domain.services;

import com.resolum.intiva.platform.communications.domain.model.aggregates.Notification;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationByIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationsByRecipientUserIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetUnreadNotificationsByRecipientUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application-facing query contract for persisted notifications.
 */
public interface NotificationQueryService {

    /**
     * Retrieves one notification by id.
     *
     * @param query identifier query
     * @return matching notification if it exists
     */
    Optional<Notification> handle(GetNotificationByIdQuery query);

    /**
     * Retrieves all notifications of one recipient user.
     *
     * @param query recipient query
     * @return notifications belonging to the given user
     */
    List<Notification> handle(GetNotificationsByRecipientUserIdQuery query);

    /**
     * Retrieves unread notifications of one recipient user.
     *
     * @param query recipient query
     * @return unread notifications belonging to the given user
     */
    List<Notification> handle(GetUnreadNotificationsByRecipientUserIdQuery query);
}

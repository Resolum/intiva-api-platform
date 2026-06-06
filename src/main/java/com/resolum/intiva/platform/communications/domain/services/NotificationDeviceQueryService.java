package com.resolum.intiva.platform.communications.domain.services;

import com.resolum.intiva.platform.communications.domain.model.aggregates.NotificationDevice;
import com.resolum.intiva.platform.communications.domain.model.queries.GetActiveNotificationDevicesByUserIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationDeviceByIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationDevicesByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application-facing query contract for registered device tokens.
 */
public interface NotificationDeviceQueryService {

    /**
     * Retrieves one registered device token by identifier.
     *
     * @param query identifier query
     * @return registered device if it exists
     */
    Optional<NotificationDevice> handle(GetNotificationDeviceByIdQuery query);

    /**
     * Retrieves all device tokens registered by one user.
     *
     * @param query user filter query
     * @return registered devices of the given user
     */
    List<NotificationDevice> handle(GetNotificationDevicesByUserIdQuery query);

    /**
     * Retrieves active device tokens of one user.
     *
     * @param query user filter query
     * @return active registered devices of the given user
     */
    List<NotificationDevice> handle(GetActiveNotificationDevicesByUserIdQuery query);
}

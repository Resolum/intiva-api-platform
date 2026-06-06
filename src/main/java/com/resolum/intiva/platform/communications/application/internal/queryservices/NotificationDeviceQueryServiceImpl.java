package com.resolum.intiva.platform.communications.application.internal.queryservices;

import com.resolum.intiva.platform.communications.domain.model.aggregates.NotificationDevice;
import com.resolum.intiva.platform.communications.domain.model.queries.GetActiveNotificationDevicesByUserIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationDeviceByIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationDevicesByUserIdQuery;
import com.resolum.intiva.platform.communications.domain.services.NotificationDeviceQueryService;
import com.resolum.intiva.platform.communications.infrastructure.persistence.jpa.repositories.NotificationDeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Default query implementation for registered device tokens.
 */
@Service
public class NotificationDeviceQueryServiceImpl implements NotificationDeviceQueryService {

    /**
     * Repository used to retrieve registered device tokens.
     */
    private final NotificationDeviceRepository notificationDeviceRepository;

    /**
     * Creates the query service with its repository dependency.
     *
     * @param notificationDeviceRepository device-token repository
     */
    public NotificationDeviceQueryServiceImpl(NotificationDeviceRepository notificationDeviceRepository) {
        this.notificationDeviceRepository = notificationDeviceRepository;
    }

    /**
     * Retrieves one device-token registration by identifier.
     */
    @Override
    public Optional<NotificationDevice> handle(GetNotificationDeviceByIdQuery query) {
        return notificationDeviceRepository.findById(query.notificationDeviceId());
    }

    /**
     * Retrieves all device-token registrations of one user.
     */
    @Override
    public List<NotificationDevice> handle(GetNotificationDevicesByUserIdQuery query) {
        return notificationDeviceRepository.findByUserIdOrderByUpdatedAtDesc(query.userId());
    }

    /**
     * Retrieves active device-token registrations of one user.
     */
    @Override
    public List<NotificationDevice> handle(GetActiveNotificationDevicesByUserIdQuery query) {
        return notificationDeviceRepository.findByUserIdAndActiveTrueOrderByUpdatedAtDesc(query.userId());
    }
}

package com.resolum.intiva.platform.communications.domain.services;

import com.resolum.intiva.platform.communications.domain.model.aggregates.NotificationDevice;
import com.resolum.intiva.platform.communications.domain.model.commands.DeactivateNotificationDeviceCommand;
import com.resolum.intiva.platform.communications.domain.model.commands.RegisterNotificationDeviceCommand;

import java.util.Optional;

/**
 * Application-facing command contract for device-token registrations in the communications context.
 */
public interface NotificationDeviceCommandService {

    /**
     * Registers a new device token or reactivates an existing one for the same user.
     *
     * @param command device registration command
     * @return created or updated device token aggregate
     */
    Optional<NotificationDevice> handle(RegisterNotificationDeviceCommand command);

    /**
     * Deactivates one registered device token.
     *
     * @param command device deactivation command
     * @return updated device token aggregate
     */
    Optional<NotificationDevice> handle(DeactivateNotificationDeviceCommand command);
}

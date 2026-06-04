package com.resolum.intiva.platform.communications.application.internal.commandservices;

import com.resolum.intiva.platform.communications.domain.model.aggregates.NotificationDevice;
import com.resolum.intiva.platform.communications.domain.model.commands.DeactivateNotificationDeviceCommand;
import com.resolum.intiva.platform.communications.domain.model.commands.RegisterNotificationDeviceCommand;
import com.resolum.intiva.platform.communications.domain.services.NotificationDeviceCommandService;
import com.resolum.intiva.platform.communications.infrastructure.persistence.jpa.repositories.NotificationDeviceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Default command implementation for registered device tokens.
 */
@Service
public class NotificationDeviceCommandServiceImpl implements NotificationDeviceCommandService {

    /**
     * Repository used to persist and retrieve device-token registrations.
     */
    private final NotificationDeviceRepository notificationDeviceRepository;

    /**
     * Creates the command service with its repository dependency.
     *
     * @param notificationDeviceRepository device-token repository
     */
    public NotificationDeviceCommandServiceImpl(NotificationDeviceRepository notificationDeviceRepository) {
        this.notificationDeviceRepository = notificationDeviceRepository;
    }

    /**
     * Registers a new token or reactivates an existing token owned by the same user.
     */
    @Override
    public Optional<NotificationDevice> handle(RegisterNotificationDeviceCommand command) {
        var existingRegistration = notificationDeviceRepository.findByUserIdAndDeviceToken(
                command.userId(),
                command.deviceToken()
        );

        if (existingRegistration.isPresent()) {
            var notificationDevice = existingRegistration.get();
            notificationDevice.reactivate(command.platform());
            return Optional.of(notificationDeviceRepository.save(notificationDevice));
        }

        var notificationDevice = new NotificationDevice(
                command.userId(),
                command.deviceToken(),
                command.platform()
        );
        return Optional.of(notificationDeviceRepository.save(notificationDevice));
    }

    /**
     * Deactivates one existing device-token registration.
     */
    @Override
    public Optional<NotificationDevice> handle(DeactivateNotificationDeviceCommand command) {
        var notificationDevice = notificationDeviceRepository.findById(command.notificationDeviceId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notification device with ID " + command.notificationDeviceId() + " does not exist."
                ));

        notificationDevice.deactivate();
        return Optional.of(notificationDeviceRepository.save(notificationDevice));
    }
}

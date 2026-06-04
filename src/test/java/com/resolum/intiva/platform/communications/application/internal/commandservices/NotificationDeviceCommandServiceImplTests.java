package com.resolum.intiva.platform.communications.application.internal.commandservices;

import com.resolum.intiva.platform.communications.domain.model.aggregates.NotificationDevice;
import com.resolum.intiva.platform.communications.domain.model.commands.DeactivateNotificationDeviceCommand;
import com.resolum.intiva.platform.communications.domain.model.commands.RegisterNotificationDeviceCommand;
import com.resolum.intiva.platform.communications.infrastructure.persistence.jpa.repositories.NotificationDeviceRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link NotificationDeviceCommandServiceImpl}.
 */
class NotificationDeviceCommandServiceImplTests {

    /**
     * Verifies that a new device-token registration is persisted when no previous registration exists.
     */
    @Test
    void handleRegister_shouldCreateDevice_whenRegistrationDoesNotExist() {
        // Arrange
        var repository = mock(NotificationDeviceRepository.class);
        var service = new NotificationDeviceCommandServiceImpl(repository);
        var command = new RegisterNotificationDeviceCommand(7L, "token-123", "android");

        when(repository.findByUserIdAndDeviceToken(7L, "token-123")).thenReturn(Optional.empty());
        when(repository.save(any(NotificationDevice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = service.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(7L, result.get().getUserId());
        assertEquals("token-123", result.get().getDeviceToken());
        assertEquals("ANDROID", result.get().getPlatform());
        assertTrue(result.get().getActive());
        verify(repository).save(any(NotificationDevice.class));
    }

    /**
     * Verifies that an existing registration is reactivated instead of duplicated.
     */
    @Test
    void handleRegister_shouldReactivateExistingDevice_whenRegistrationAlreadyExists() {
        // Arrange
        var repository = mock(NotificationDeviceRepository.class);
        var service = new NotificationDeviceCommandServiceImpl(repository);
        var existingDevice = new NotificationDevice(7L, "token-123", "ios");
        existingDevice.deactivate();

        when(repository.findByUserIdAndDeviceToken(7L, "token-123")).thenReturn(Optional.of(existingDevice));
        when(repository.save(existingDevice)).thenReturn(existingDevice);

        // Act
        var result = service.handle(new RegisterNotificationDeviceCommand(7L, "token-123", "android"));

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getActive());
        assertEquals("ANDROID", result.get().getPlatform());
        verify(repository).save(existingDevice);
    }

    /**
     * Verifies that a deactivation command turns the registration inactive.
     */
    @Test
    void handleDeactivate_shouldDeactivateExistingDevice() {
        // Arrange
        var repository = mock(NotificationDeviceRepository.class);
        var service = new NotificationDeviceCommandServiceImpl(repository);
        var existingDevice = new NotificationDevice(7L, "token-123", "android");

        when(repository.findById(5L)).thenReturn(Optional.of(existingDevice));
        when(repository.save(existingDevice)).thenReturn(existingDevice);

        // Act
        var result = service.handle(new DeactivateNotificationDeviceCommand(5L));

        // Assert
        assertTrue(result.isPresent());
        assertFalse(result.get().getActive());
        verify(repository).save(existingDevice);
    }
}

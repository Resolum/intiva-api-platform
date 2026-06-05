package com.resolum.intiva.platform.communications.domain.model.aggregates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDeviceTests {

    @Test
    void reassignTo_shouldUpdateOwnerAndReactivateDevice_whenParametersAreValid() {
        // Arrange
        var notificationDevice = new NotificationDevice(1L, "device-token-123", "android");
        notificationDevice.deactivate();

        // Act
        notificationDevice.reassignTo(2L, "ios");

        // Assert
        assertEquals(2L, notificationDevice.getUserId());
        assertEquals("device-token-123", notificationDevice.getDeviceToken());
        assertEquals("IOS", notificationDevice.getPlatform());
        assertTrue(notificationDevice.getActive());
    }

    @Test
    void reassignTo_shouldThrowException_whenUserIdIsNull() {
        // Arrange
        var notificationDevice = new NotificationDevice(1L, "device-token-123", "android");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationDevice.reassignTo(null, "ios"));
    }

    @Test
    void reassignTo_shouldThrowException_whenPlatformIsBlank() {
        // Arrange
        var notificationDevice = new NotificationDevice(1L, "device-token-123", "android");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationDevice.reassignTo(2L, " "));
    }
}

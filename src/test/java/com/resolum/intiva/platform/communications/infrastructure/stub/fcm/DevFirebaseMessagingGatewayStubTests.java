package com.resolum.intiva.platform.communications.infrastructure.stub.fcm;

import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DevFirebaseMessagingGatewayStub}.
 *
 * <p>The stub is intentionally simple: when push delivery is disabled, it should avoid talking to
 * Firebase and leave a clear log entry that helps verify the backend flow during development.</p>
 */
@ExtendWith(OutputCaptureExtension.class)
class DevFirebaseMessagingGatewayStubTests {

    /**
     * Verifies that the development stub logs the skipped push delivery instead of attempting a real send.
     *
     * @param output captured console output for the test execution
     */
    @Test
    void send_shouldLogSkippedPushDelivery(CapturedOutput output) {
        // Arrange
        var stub = new DevFirebaseMessagingGatewayStub();
        var command = new SendPushNotificationCommand(
                7L,
                "device-token-123",
                "PAYMENT_DUE_SOON",
                "PAYMENT_REMINDER",
                10L,
                "Pago proximo a vencer",
                "Tu pago de Netflix vence manana"
        );

        // Act
        stub.send(command);

        // Assert
        assertTrue(output.getOut().contains("FCM integration disabled. Push notification not sent"));
        assertTrue(output.getOut().contains("device-token-123"));
        assertTrue(output.getOut().contains("Pago proximo a vencer"));
        assertTrue(output.getOut().contains("7"));
    }
}

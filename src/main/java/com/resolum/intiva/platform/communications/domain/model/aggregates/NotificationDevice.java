package com.resolum.intiva.platform.communications.domain.model.aggregates;

import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Aggregate that represents one mobile device token registered for push notifications.
 *
 * <p>A single user can register multiple devices, each with its own token and platform.</p>
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "notification_devices")
public class NotificationDevice extends AuditableAbstractAggregate<NotificationDevice> {

    /**
     * User who owns the registered device token.
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * Firebase Cloud Messaging token associated with one app installation.
     */
    @Column(name = "device_token", nullable = false, length = 512, unique = true)
    private String deviceToken;

    /**
     * Client platform that registered the token, such as ANDROID or IOS.
     */
    @Column(nullable = false, length = 32)
    private String platform;

    /**
     * Whether the device token is currently active for push delivery.
     */
    @Column(nullable = false)
    private Boolean active;

    /**
     * Creates a new registered notification device.
     *
     * @param userId owner of the token
     * @param deviceToken Firebase token
     * @param platform client platform
     */
    public NotificationDevice(Long userId, String deviceToken, String platform) {
        validate(userId, deviceToken, platform);
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.platform = platform.toUpperCase();
        this.active = true;
    }

    /**
     * Reactivates and refreshes the platform metadata of the existing token.
     *
     * @param platform latest platform sent by the client
     */
    public void reactivate(String platform) {
        if (platform == null || platform.isBlank()) {
            throw new IllegalArgumentException("Platform is required.");
        }
        this.platform = platform.toUpperCase();
        this.active = true;
    }

    /**
     * Reassigns this app installation token to the currently signed-in user.
     *
     * @param userId latest user who owns the app session
     * @param platform latest platform sent by the client
     */
    public void reassignTo(Long userId, String platform) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required.");
        }
        this.userId = userId;
        reactivate(platform);
    }

    /**
     * Deactivates the device token so it is ignored for future push deliveries.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Validates the minimum required data for a device-token registration.
     *
     * @param userId owner of the token
     * @param deviceToken token to validate
     * @param platform platform to validate
     */
    private void validate(Long userId, String deviceToken, String platform) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required.");
        }
        if (deviceToken == null || deviceToken.isBlank()) {
            throw new IllegalArgumentException("Device token is required.");
        }
        if (platform == null || platform.isBlank()) {
            throw new IllegalArgumentException("Platform is required.");
        }
    }
}

package com.resolum.intiva.platform.communications.infrastructure.providers.fcm.services;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.resolum.intiva.platform.communications.application.internal.outboundservices.fcm.FirebaseMessagingGateway;
import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;
import com.resolum.intiva.platform.communications.infrastructure.persistence.jpa.repositories.NotificationDeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "integrations.fcm.enabled", havingValue = "true")
public class FirebaseMessagingGatewayImpl implements FirebaseMessagingGateway {

    private final FirebaseApp firebaseApp;
    private final NotificationDeviceRepository notificationDeviceRepository;

    public FirebaseMessagingGatewayImpl(
            FirebaseApp firebaseApp,
            NotificationDeviceRepository notificationDeviceRepository
    ) {
        this.firebaseApp = firebaseApp;
        this.notificationDeviceRepository = notificationDeviceRepository;
    }

    @Override
    public void send(SendPushNotificationCommand command) {
        try {
            var message = Message.builder()
                    .setToken(command.deviceToken())
                    .setNotification(Notification.builder()
                            .setTitle(command.title())
                            .setBody(command.message())
                            .build())
                    .putData("type", command.type())
                    .putData("source", command.source())
                    .putData("sourceId", String.valueOf(command.sourceId()))
                    .putData("recipientUserId", String.valueOf(command.recipientUserId()))
                    .build();

            var response = FirebaseMessaging.getInstance(firebaseApp).send(message);
            log.info("Push notification sent successfully. Firebase message id={}", response);
        } catch (FirebaseMessagingException exception) {
            log.error("Failed to send push notification. user={}, tokenPrefix={}, errorCode={}, message={}",
                    command.recipientUserId(),
                    maskToken(command.deviceToken()),
                    exception.getMessagingErrorCode(),
                    exception.getMessage(),
                    exception);

            if (exception.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                deactivateUnregisteredDevice(command);
            }
        } catch (Exception exception) {
            log.error("Failed to send push notification to user {} with tokenPrefix={}",
                    command.recipientUserId(),
                    maskToken(command.deviceToken()),
                    exception);
        }
    }

    private void deactivateUnregisteredDevice(SendPushNotificationCommand command) {
        notificationDeviceRepository
                .findByUserIdAndDeviceToken(command.recipientUserId(), command.deviceToken())
                .ifPresentOrElse(device -> {
                    device.deactivate();
                    notificationDeviceRepository.save(device);
                    log.warn("Deactivated unregistered FCM device token. user={}, notificationDeviceId={}, tokenPrefix={}",
                            command.recipientUserId(),
                            device.getId(),
                            maskToken(command.deviceToken()));
                }, () -> log.warn("UNREGISTERED FCM token was not found in database. user={}, tokenPrefix={}",
                        command.recipientUserId(),
                        maskToken(command.deviceToken())));
    }

    private String maskToken(String deviceToken) {
        if (deviceToken == null || deviceToken.length() < 12) {
            return "***";
        }
        return deviceToken.substring(0, 8) + "...";
    }
}

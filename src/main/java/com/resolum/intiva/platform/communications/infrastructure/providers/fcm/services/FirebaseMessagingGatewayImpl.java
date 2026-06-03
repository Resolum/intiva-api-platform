package com.resolum.intiva.platform.communications.infrastructure.providers.fcm.services;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.resolum.intiva.platform.communications.application.internal.outboundservices.fcm.FirebaseMessagingGateway;
import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "integrations.fcm.enabled", havingValue = "true")
public class FirebaseMessagingGatewayImpl implements FirebaseMessagingGateway {

    private final FirebaseApp firebaseApp;

    public FirebaseMessagingGatewayImpl(FirebaseApp firebaseApp) {
        this.firebaseApp = firebaseApp;
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
        } catch (Exception exception) {
            log.error("Failed to send push notification to user {} with token {}",
                    command.recipientUserId(),
                    command.deviceToken(),
                    exception);
            throw new IllegalStateException("Could not send push notification.", exception);
        }
    }
}

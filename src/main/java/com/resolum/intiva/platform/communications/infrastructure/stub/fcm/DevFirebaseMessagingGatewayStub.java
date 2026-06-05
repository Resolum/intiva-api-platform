package com.resolum.intiva.platform.communications.infrastructure.stub.fcm;

import com.resolum.intiva.platform.communications.application.internal.outboundservices.fcm.FirebaseMessagingGateway;
import com.resolum.intiva.platform.communications.domain.model.commands.SendPushNotificationCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "integrations.fcm.enabled", havingValue = "false", matchIfMissing = true)
public class DevFirebaseMessagingGatewayStub implements FirebaseMessagingGateway {
    @Override
    public void send(
            Long recipientUserId,
            String deviceToken,
            String type,
            String source,
            Long sourceId,
            String title,
            String message
    ) {
        log.info("FCM integration disabled. Push notification not sent (deviceToken={}, title={} for user={})",
                deviceToken, title, recipientUserId);
    }
}

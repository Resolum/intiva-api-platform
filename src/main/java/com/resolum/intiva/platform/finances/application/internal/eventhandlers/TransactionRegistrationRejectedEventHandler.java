package com.resolum.intiva.platform.finances.application.internal.eventhandlers;

import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationSource;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationType;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalNotificationsService;
import com.resolum.intiva.platform.finances.domain.model.events.TransactionRegistrationRejectedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Sends user notifications when a transaction registration is rejected.
 */
@Component
@Slf4j
public class TransactionRegistrationRejectedEventHandler {

    private final FinancesExternalNotificationsService financesExternalNotificationsService;

    public TransactionRegistrationRejectedEventHandler(
            FinancesExternalNotificationsService financesExternalNotificationsService
    ) {
        this.financesExternalNotificationsService = financesExternalNotificationsService;
    }

    @EventListener
    public void handle(TransactionRegistrationRejectedEvent event) {
        var title = "Transaccion no registrada";
        var message = event.getReason() == null || event.getReason().isBlank()
                ? "No pudimos registrar tu transaccion."
                : event.getReason();

        log.info(
                "Sending transaction rejection notification. recipientUserId={}, financialAccountId={}, clientOperationId={}",
                event.getRecipientUserId(),
                event.getFinancialAccountId(),
                event.getClientOperationId()
        );

        try {
            financesExternalNotificationsService.sendPushNotificationToUser(
                    event.getRecipientUserId(),
                    NotificationType.TRANSACTION_REJECTED.name(),
                    NotificationSource.TRANSACTION.name(),
                    event.getFinancialAccountId(),
                    title,
                    message
            );
        } catch (Exception exception) {
            log.warn(
                    "Transaction rejection push notification could not be sent. recipientUserId={}, financialAccountId={}",
                    event.getRecipientUserId(),
                    event.getFinancialAccountId(),
                    exception
            );
        }

        try {
            financesExternalNotificationsService.createInAppNotification(
                    event.getRecipientUserId(),
                    NotificationType.TRANSACTION_REJECTED.name(),
                    NotificationSource.TRANSACTION.name(),
                    event.getFinancialAccountId(),
                    title,
                    message
            );
        } catch (Exception exception) {
            log.warn(
                    "Transaction rejection in-app notification could not be persisted. recipientUserId={}, financialAccountId={}",
                    event.getRecipientUserId(),
                    event.getFinancialAccountId(),
                    exception
            );
        }
    }
}

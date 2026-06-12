package com.resolum.intiva.platform.communications.application.internal.eventhandlers;

import com.resolum.intiva.platform.communications.interfaces.acl.CommunicationsContextFacade;
import com.resolum.intiva.platform.finances.domain.model.events.PaymentDueSoonEvent;
import com.resolum.intiva.platform.finances.domain.model.events.PaymentExpiredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentReminderEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentReminderEventHandler.class);

    private final CommunicationsContextFacade communicationsContextFacade;

    public PaymentReminderEventHandler(CommunicationsContextFacade communicationsContextFacade) {
        this.communicationsContextFacade = communicationsContextFacade;
    }

    @EventListener
    public void on(PaymentDueSoonEvent event) {
        LOGGER.info("Handling PaymentDueSoonEvent: recurringTransactionId={}, actorUserId={}",
                event.getRecurringTransactionId(), event.getActorUserId());

        var message = "Tu pago de " + event.getTransactionDescription()
                + " vence el " + event.getEndsAt().toString();

        communicationsContextFacade.createInAppNotification(
                event.getActorUserId(),
                "PAYMENT_DUE_SOON",
                "PAYMENT_REMINDER",
                event.getRecurringTransactionId(),
                "Pago próximo a vencer",
                message
        );

        communicationsContextFacade.sendPushNotificationToUser(
                event.getActorUserId(),
                "PAYMENT_DUE_SOON",
                "PAYMENT_REMINDER",
                event.getRecurringTransactionId(),
                "Pago próximo a vencer",
                message
        );

        LOGGER.info("Payment due-soon notification sent to userId={}", event.getActorUserId());
    }

    @EventListener
    public void on(PaymentExpiredEvent event) {
        LOGGER.info("Handling PaymentExpiredEvent: recurringTransactionId={}, actorUserId={}",
                event.getRecurringTransactionId(), event.getActorUserId());

        var message = "Tu pago de " + event.getTransactionDescription()
                + " venció el " + event.getEndsAt().toString();

        communicationsContextFacade.createInAppNotification(
                event.getActorUserId(),
                "PAYMENT_OVERDUE",
                "PAYMENT_REMINDER",
                event.getRecurringTransactionId(),
                "Pago vencido",
                message
        );

        communicationsContextFacade.sendPushNotificationToUser(
                event.getActorUserId(),
                "PAYMENT_OVERDUE",
                "PAYMENT_REMINDER",
                event.getRecurringTransactionId(),
                "Pago vencido",
                message
        );

        LOGGER.info("Payment expired notification sent to userId={}", event.getActorUserId());
    }
}

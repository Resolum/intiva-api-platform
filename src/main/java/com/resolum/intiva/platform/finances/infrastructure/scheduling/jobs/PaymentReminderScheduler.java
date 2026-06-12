package com.resolum.intiva.platform.finances.infrastructure.scheduling.jobs;

import com.resolum.intiva.platform.finances.application.internal.jobhandlers.PaymentReminderJobHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentReminderScheduler {

    private final PaymentReminderJobHandler paymentReminderJobHandler;

    public PaymentReminderScheduler(PaymentReminderJobHandler paymentReminderJobHandler) {
        this.paymentReminderJobHandler = paymentReminderJobHandler;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void processPaymentReminders() {
        paymentReminderJobHandler.processPaymentReminders();
    }
}

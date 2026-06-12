package com.resolum.intiva.platform.finances.application.internal.jobhandlers;

import com.resolum.intiva.platform.finances.domain.model.events.PaymentDueSoonEvent;
import com.resolum.intiva.platform.finances.domain.model.events.PaymentExpiredEvent;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PaymentReminderJobHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentReminderJobHandler.class);

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentReminderJobHandler(
            RecurringTransactionRepository recurringTransactionRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public void processPaymentReminders() {
        var today = LocalDate.now();
        var transactions = recurringTransactionRepository.findAllByActiveTrueAndEndDateIsNotNull();

        for (var transaction : transactions) {
            var endDate = transaction.getEndDate();
            if (endDate == null) continue;

            var reminderDays = transaction.getReminderDaysBefore() != null
                    ? transaction.getReminderDaysBefore()
                    : 3;

            if (endDate.isBefore(today)) {
                publishExpiredEvent(transaction);
            } else if (!endDate.isAfter(today.plusDays(reminderDays - 1))) {
                publishDueSoonEvent(transaction);
            }
        }
    }

    private void publishDueSoonEvent(com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction transaction) {
        LOGGER.info("Publishing PaymentDueSoonEvent for recurring transaction ID={}", transaction.getId());
        eventPublisher.publishEvent(new PaymentDueSoonEvent(
                this,
                transaction.getId(),
                transaction.getPerformedByUserId().getValue(),
                transaction.getOwnerId(),
                transaction.getOwnerType(),
                transaction.getDescription(),
                transaction.getAmount().amount(),
                transaction.getEndDate()
        ));
    }

    private void publishExpiredEvent(com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction transaction) {
        LOGGER.info("Publishing PaymentExpiredEvent for recurring transaction ID={}", transaction.getId());
        eventPublisher.publishEvent(new PaymentExpiredEvent(
                this,
                transaction.getId(),
                transaction.getPerformedByUserId().getValue(),
                transaction.getOwnerId(),
                transaction.getOwnerType(),
                transaction.getDescription(),
                transaction.getAmount().amount(),
                transaction.getEndDate()
        ));
    }
}

package com.resolum.intiva.platform.finances.domain.model.commands;

public record UpdatePaymentReminderCommand(
        Long recurringTransactionId,
        int reminderDaysBefore
) {
    public UpdatePaymentReminderCommand {
        if (recurringTransactionId == null) {
            throw new IllegalArgumentException("Recurring transaction ID cannot be null");
        }
        if (reminderDaysBefore != 1 && reminderDaysBefore != 3 && reminderDaysBefore != 7) {
            throw new IllegalArgumentException("reminderDaysBefore must be 1, 3, or 7");
        }
    }
}

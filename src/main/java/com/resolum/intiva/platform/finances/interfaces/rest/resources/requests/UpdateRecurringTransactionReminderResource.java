package com.resolum.intiva.platform.finances.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body to update the reminder configuration for a recurring transaction.")
public record UpdateRecurringTransactionReminderResource(
        @Schema(description = "Days before the end date to send a reminder. Allowed values: 1, 3, 7.", example = "3")
        int reminderDaysBefore
) {
}

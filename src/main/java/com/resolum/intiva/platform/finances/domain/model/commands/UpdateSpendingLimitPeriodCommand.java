package com.resolum.intiva.platform.finances.domain.model.commands;

import java.time.LocalDate;

/**
 * Command used to update the active date range of an existing spending limit.
 *
 * @param spendingLimitId spending limit identifier
 * @param startDate first included date
 * @param endDate last included date
 */
public record UpdateSpendingLimitPeriodCommand(
        Long spendingLimitId,
        LocalDate startDate,
        LocalDate endDate
) {
}

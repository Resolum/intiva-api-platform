package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * REST response for an income vs expense trend period.
 *
 * @param periodStart   period start date
 * @param periodEnd     period end date
 * @param periodType    period type (DAILY, WEEKLY, MONTHLY, ANNUAL)
 * @param totalIncome   total income for the period
 * @param totalExpenses total expenses for the period
 * @param netBalance    net balance for the period
 */
@Schema(description = "REST response for an income vs expense trend period.")
public record AnalyticsPeriodTrendResource(
        @Schema(description = "Period start date.", example = "2026-01-01") LocalDate periodStart,
        @Schema(description = "Period end date.", example = "2026-01-31") LocalDate periodEnd,
        @Schema(description = "Period type.", example = "MONTHLY", allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ANNUAL"}) String periodType,
        @Schema(description = "Total income for the period.") MoneyResource totalIncome,
        @Schema(description = "Total expenses for the period.") MoneyResource totalExpenses,
        @Schema(description = "Net balance for the period.") MoneyResource netBalance
) {
}

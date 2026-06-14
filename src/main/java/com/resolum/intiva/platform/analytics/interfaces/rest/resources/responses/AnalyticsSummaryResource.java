package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * REST response returned for analytics summary operations.
 *
 * @param ownerId            owner identifier
 * @param ownerType          owner scope (INDIVIDUAL or FAMILY)
 * @param periodType         period granularity (DAILY, WEEKLY, MONTHLY, ANNUAL)
 * @param periodStart        period start date
 * @param periodEnd          period end date
 * @param totalIncome        total income amount
 * @param totalExpenses      total expenses amount
 * @param netBalance         net balance (income - expenses)
 * @param savingsRate        savings rate percentage
 * @param expensesByCategory expenses grouped by category
 * @param generatedAt        timestamp when this summary was generated
 */
@Schema(description = "REST response for an analytics summary.")
public record AnalyticsSummaryResource(
        @Schema(description = "Owner identifier.", example = "1") String ownerId,
        @Schema(description = "Owner scope.", example = "INDIVIDUAL", allowableValues = {"INDIVIDUAL", "FAMILY"}) String ownerType,
        @Schema(description = "Period type.", example = "MONTHLY", allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "ANNUAL"}) String periodType,
        @Schema(description = "Period start date.", example = "2026-06-01") LocalDate periodStart,
        @Schema(description = "Period end date.", example = "2026-06-30") LocalDate periodEnd,
        @Schema(description = "Total income amount.") MoneyResource totalIncome,
        @Schema(description = "Total expenses amount.") MoneyResource totalExpenses,
        @Schema(description = "Net balance (income - expenses).") MoneyResource netBalance,
        @Schema(description = "Savings rate percentage.", example = "15.50") BigDecimal savingsRate,
        @Schema(description = "Expenses grouped by category.") List<CategoryExpenseSummaryResource> expensesByCategory,
        @Schema(description = "Timestamp when this summary was generated.", example = "2026-06-13T18:00:00Z") Instant generatedAt
) {
}

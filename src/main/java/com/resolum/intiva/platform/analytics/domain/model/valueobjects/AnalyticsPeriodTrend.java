package com.resolum.intiva.platform.analytics.domain.model.valueobjects;

import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Income vs expense trend for a specific analytics period.
 *
 * @param period       the analytics period this trend belongs to
 * @param totalIncome  total income during the period
 * @param totalExpenses total expenses during the period
 * @param netBalance   net balance (income minus expenses) for the period
 */
@Schema(description = "Income vs expense trend for a specific period.")
public record AnalyticsPeriodTrend(
        @Schema(description = "Analytics period details.") AnalyticsPeriod period,
        @Schema(description = "Total income during the period.") Money totalIncome,
        @Schema(description = "Total expenses during the period.") Money totalExpenses,
        @Schema(description = "Net balance (income minus expenses) for the period.") Money netBalance
) {
}

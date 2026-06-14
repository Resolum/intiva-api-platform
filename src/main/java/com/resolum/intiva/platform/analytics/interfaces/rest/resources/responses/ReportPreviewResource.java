package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Report summary preview response.")
public record ReportPreviewResource(
        @Schema(description = "Total income.") MoneyResource totalIncome,
        @Schema(description = "Total expenses.") MoneyResource totalExpenses,
        @Schema(description = "Net balance.") MoneyResource netBalance,
        @Schema(description = "Total transaction count.", example = "42") int transactionCount,
        @Schema(description = "Top expense categories.") List<ReportSummaryItemResource> topCategories,
        @Schema(description = "Period start date.", example = "2026-01-01") String periodStart,
        @Schema(description = "Period end date.", example = "2026-06-30") String periodEnd,
        @Schema(description = "Owner identifier.", example = "1") String ownerId,
        @Schema(description = "Owner scope.", example = "INDIVIDUAL") String ownerType
) {
}

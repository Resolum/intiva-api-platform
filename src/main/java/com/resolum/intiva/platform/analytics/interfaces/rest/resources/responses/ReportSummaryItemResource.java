package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Report summary item for a single category.")
public record ReportSummaryItemResource(
        @Schema(description = "Category identifier.", example = "1") String categoryId,
        @Schema(description = "Category display name.", example = "Alimentacion") String categoryName,
        @Schema(description = "Category color in hex format.", example = "#FF5733") String categoryColor,
        @Schema(description = "Total amount spent in this category.") MoneyResource totalAmount,
        @Schema(description = "Number of transactions in this category.", example = "12") int transactionCount,
        @Schema(description = "Percentage of total expenses.", example = "25.50") String percentage
) {
}

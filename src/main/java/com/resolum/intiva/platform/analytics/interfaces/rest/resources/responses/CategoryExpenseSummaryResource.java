package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * REST response for a category expense summary in analytics.
 *
 * @param categoryId      category identifier
 * @param categoryName    category display name
 * @param categoryColor   category color in hex format
 * @param totalAmount     total amount spent
 * @param transactionCount number of transactions
 * @param percentage      percentage of total expenses
 */
@Schema(description = "REST response for a category expense summary.")
public record CategoryExpenseSummaryResource(
        @Schema(description = "Category identifier.", example = "1") String categoryId,
        @Schema(description = "Category display name.", example = "Alimentacion") String categoryName,
        @Schema(description = "Category color in hex format.", example = "#FF5733") String categoryColor,
        @Schema(description = "Total amount spent.") MoneyResource totalAmount,
        @Schema(description = "Number of transactions.", example = "12") Integer transactionCount,
        @Schema(description = "Percentage of total expenses.", example = "25.50") BigDecimal percentage
) {
}

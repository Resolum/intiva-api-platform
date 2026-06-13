package com.resolum.intiva.platform.analytics.domain.model.valueobjects;

import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Summary of expenses grouped by a single category.
 *
 * @param categoryId        category identifier
 * @param categoryName      display name of the category
 * @param categoryColor     hex color code of the category
 * @param totalAmount       total amount spent in this category
 * @param transactionCount  number of transactions in this category
 * @param percentage        percentage this category represents of total expenses
 */
@Schema(description = "Summary of expenses grouped by a single category.")
public record CategoryExpenseSummary(
        @Schema(description = "Category identifier.", example = "1") String categoryId,
        @Schema(description = "Category display name.", example = "Alimentacion") String categoryName,
        @Schema(description = "Category color in hex format.", example = "#FF5733") String categoryColor,
        @Schema(description = "Total amount spent in this category.") Money totalAmount,
        @Schema(description = "Number of transactions in this category.", example = "12") Integer transactionCount,
        @Schema(description = "Percentage this category represents of total expenses.", example = "25.50") BigDecimal percentage
) {
}

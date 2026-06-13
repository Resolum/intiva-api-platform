package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * REST response for a spending limit detail in analytics.
 *
 * @param spendingLimitId spending limit identifier
 * @param categoryId      category identifier
 * @param categoryName    category display name
 * @param limitAmount     maximum limit amount
 * @param currentAmount   current consumed amount
 * @param usagePercentage usage percentage
 * @param status          analytics status (SAFE, WARNING, EXCEEDED)
 */
@Schema(description = "REST response for a spending limit detail in analytics.")
public record SpendingLimitDetailResource(
        @Schema(description = "Spending limit identifier.", example = "1") String spendingLimitId,
        @Schema(description = "Category identifier.", example = "1") String categoryId,
        @Schema(description = "Category display name.", example = "Entretenimiento") String categoryName,
        @Schema(description = "Maximum limit amount.") MoneyResource limitAmount,
        @Schema(description = "Current consumed amount.") MoneyResource currentAmount,
        @Schema(description = "Usage percentage.", example = "75.00") BigDecimal usagePercentage,
        @Schema(description = "Analytics status.", example = "WARNING", allowableValues = {"SAFE", "WARNING", "EXCEEDED"}) String status
) {
}

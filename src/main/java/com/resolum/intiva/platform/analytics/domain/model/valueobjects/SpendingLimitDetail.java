package com.resolum.intiva.platform.analytics.domain.model.valueobjects;

import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Detailed information about a single spending limit for analytics purposes.
 *
 * @param spendingLimitId  spending limit identifier
 * @param categoryId       category identifier associated with the limit
 * @param categoryName     display name of the category
 * @param limitAmount      maximum configured amount for the limit
 * @param currentAmount    amount already consumed
 * @param usagePercentage  usage percentage relative to the limit
 * @param status           analytics status derived from usage percentage
 */
@Schema(description = "Detailed information about a single spending limit for analytics purposes.")
public record SpendingLimitDetail(
        @Schema(description = "Spending limit identifier.", example = "1") String spendingLimitId,
        @Schema(description = "Category identifier associated with the limit.", example = "1") String categoryId,
        @Schema(description = "Category display name.", example = "Entretenimiento") String categoryName,
        @Schema(description = "Maximum configured amount for the limit.") Money limitAmount,
        @Schema(description = "Amount already consumed.") Money currentAmount,
        @Schema(description = "Usage percentage relative to the limit.", example = "75.00") BigDecimal usagePercentage,
        @Schema(description = "Analytics status derived from usage percentage.") SpendingLimitAnalyticsStatus status
) {
}

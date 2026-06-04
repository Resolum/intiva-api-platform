package com.resolum.intiva.platform.finances.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * REST payload used to create a new spending limit.
 */
@Schema(description = "Request body used to create a spending limit for an individual or family owner.")
public record CreateSpendingLimitResource(
        @Schema(description = "Owner identifier. For INDIVIDUAL it is the user id; for FAMILY it is the family/group id.", example = "1")
        Long ownerId,

        @Schema(description = "Owner scope of the limit. INDIVIDUAL is personal finance and FAMILY is group finance.", example = "INDIVIDUAL", allowableValues = {"INDIVIDUAL", "FAMILY"})
        String ownerType,

        @Schema(description = "Target controlled by the limit. CATEGORY limits expenses by category; FINANCIAL_ACCOUNT limits expenses by payment method/account.", example = "CATEGORY", allowableValues = {"CATEGORY", "FINANCIAL_ACCOUNT"})
        String targetType,

        @Schema(description = "Target identifier. This is a category id when targetType is CATEGORY, or a financial account id when targetType is FINANCIAL_ACCOUNT.", example = "5")
        Long targetId,

        @Schema(description = "Maximum amount allowed for this limit period.", example = "500.00")
        BigDecimal limitAmount,

        @Schema(description = "Currency code used by the limit.", example = "PEN", allowableValues = {"PEN", "USD", "EUR"})
        String currencyCode,

        @Schema(description = "First date included in the spending limit period.", example = "2026-06-01")
        LocalDate startDate,

        @Schema(description = "Last date included in the spending limit period.", example = "2026-06-30")
        LocalDate endDate
) {
}

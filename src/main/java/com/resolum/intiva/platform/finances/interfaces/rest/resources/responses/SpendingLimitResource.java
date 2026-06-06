package com.resolum.intiva.platform.finances.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * REST response returned for spending limit operations.
 */
@Schema(description = "REST representation of a spending limit and its current consumption.")
public record SpendingLimitResource(
        @Schema(description = "Unique spending limit identifier.", example = "1")
        Long id,

        @Schema(description = "Owner identifier. For INDIVIDUAL it is the user id; for FAMILY it is the family/group id.", example = "1")
        Long ownerId,

        @Schema(description = "Owner scope of the limit.", example = "INDIVIDUAL", allowableValues = {"INDIVIDUAL", "FAMILY"})
        String ownerType,

        @Schema(description = "Target controlled by the limit.", example = "CATEGORY", allowableValues = {"CATEGORY", "FINANCIAL_ACCOUNT"})
        String targetType,

        @Schema(description = "Category id or financial account id controlled by this limit.", example = "5")
        Long targetId,

        @Schema(description = "Maximum amount configured for the period.", example = "500.00")
        String limitAmount,

        @Schema(description = "Amount already consumed by matching EXPENSE transactions.", example = "150.00")
        String spentAmount,

        @Schema(description = "Currency code used by both limitAmount and spentAmount.", example = "PEN")
        String currencyCode,

        @Schema(description = "First date included in the spending limit period.", example = "2026-06-01")
        String startDate,

        @Schema(description = "Last date included in the spending limit period.", example = "2026-06-30")
        String endDate,

        @Schema(description = "Whether the limit is currently evaluated when expenses are registered.", example = "true")
        Boolean active,

        @Schema(description = "Current status calculated from spentAmount and limitAmount. WARNING starts at 80 percent consumed.", example = "NORMAL", allowableValues = {"NORMAL", "WARNING", "EXCEEDED"})
        String status
) {
}

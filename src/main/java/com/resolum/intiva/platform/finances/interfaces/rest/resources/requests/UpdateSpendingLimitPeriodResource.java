package com.resolum.intiva.platform.finances.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * REST payload used to update the active period of a spending limit.
 */
@Schema(description = "Request body used to update the date range of an existing spending limit.")
public record UpdateSpendingLimitPeriodResource(
        @Schema(description = "First date included in the new spending limit period.", example = "2026-07-01")
        LocalDate startDate,

        @Schema(description = "Last date included in the new spending limit period.", example = "2026-07-31")
        LocalDate endDate
) {
}

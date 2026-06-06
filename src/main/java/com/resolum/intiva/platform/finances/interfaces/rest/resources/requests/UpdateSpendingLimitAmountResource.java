package com.resolum.intiva.platform.finances.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * REST payload used to update the maximum amount of an existing spending limit.
 */
@Schema(description = "Request body used to update the maximum amount of an existing spending limit.")
public record UpdateSpendingLimitAmountResource(
        @Schema(description = "New maximum amount for the limit. It must be greater than zero.", example = "700.00")
        BigDecimal limitAmount,

        @Schema(description = "Currency code. It must match the currency originally used by the spending limit.", example = "PEN", allowableValues = {"PEN", "USD", "EUR"})
        String currencyCode
) {
}

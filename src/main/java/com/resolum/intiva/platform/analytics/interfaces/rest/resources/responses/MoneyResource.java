package com.resolum.intiva.platform.analytics.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * REST representation of a monetary value with amount and currency code.
 *
 * @param amount       the numeric amount
 * @param currencyCode the ISO currency code (PEN, USD, EUR)
 */
@Schema(description = "Monetary value with amount and currency code.")
public record MoneyResource(
        @Schema(description = "Monetary amount.", example = "1000.00") BigDecimal amount,
        @Schema(description = "Currency code.", example = "PEN", allowableValues = {"PEN", "USD", "EUR"}) String currencyCode
) {
}

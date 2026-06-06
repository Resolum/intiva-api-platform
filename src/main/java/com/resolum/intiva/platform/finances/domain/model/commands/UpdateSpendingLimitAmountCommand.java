package com.resolum.intiva.platform.finances.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import jakarta.validation.Valid;

/**
 * Command used to update the maximum amount of an existing spending limit.
 *
 * @param spendingLimitId spending limit identifier
 * @param newLimitAmount updated ceiling for the same currency
 */
public record UpdateSpendingLimitAmountCommand(
        Long spendingLimitId,
        @Valid Money newLimitAmount
) {
}

package com.resolum.intiva.platform.finances.domain.model.commands;

/**
 * Command used to deactivate an existing spending limit.
 *
 * @param spendingLimitId spending limit identifier
 */
public record DeactivateSpendingLimitCommand(Long spendingLimitId) {
}

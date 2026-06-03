package com.resolum.intiva.platform.finances.domain.model.commands;

/**
 * Command used to activate an existing spending limit.
 *
 * @param spendingLimitId spending limit identifier
 */
public record ActivateSpendingLimitCommand(Long spendingLimitId) {
}

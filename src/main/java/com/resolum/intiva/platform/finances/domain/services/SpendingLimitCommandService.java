package com.resolum.intiva.platform.finances.domain.services;

import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.commands.*;

import java.util.Optional;

/**
 * Application-facing command contract for the SpendingLimit aggregate.
 *
 * <p>CRUD-like commands are used by the REST API. {@link RegisterExpenseAgainstSpendingLimitsCommand} is used
 * internally by the transaction registration flow to increase spentAmount for matching limits.</p>
 */
public interface SpendingLimitCommandService {
    /**
     * Creates a new spending limit.
     *
     * @param command creation command
     * @return the persisted spending limit
     */
    Optional<SpendingLimit> handle(CreateSpendingLimitCommand command);

    /**
     * Updates the maximum amount of an existing spending limit.
     *
     * @param command update command
     * @return the updated spending limit
     */
    Optional<SpendingLimit> handle(UpdateSpendingLimitAmountCommand command);

    /**
     * Updates the active period of an existing spending limit.
     *
     * @param command update command
     * @return the updated spending limit
     */
    Optional<SpendingLimit> handle(UpdateSpendingLimitPeriodCommand command);

    /**
     * Activates an existing spending limit.
     *
     * @param command activation command
     * @return the updated spending limit
     */
    Optional<SpendingLimit> handle(ActivateSpendingLimitCommand command);

    /**
     * Deactivates an existing spending limit.
     *
     * @param command deactivation command
     * @return the updated spending limit
     */
    Optional<SpendingLimit> handle(DeactivateSpendingLimitCommand command);

    /**
     * Applies an expense transaction to every matching spending limit.
     *
     * @param command internal consumption command
     */
    void handle(RegisterExpenseAgainstSpendingLimitsCommand command);
}

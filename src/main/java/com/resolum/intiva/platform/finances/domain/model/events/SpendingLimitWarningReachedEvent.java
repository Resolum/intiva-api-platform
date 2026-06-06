package com.resolum.intiva.platform.finances.domain.model.events;

import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import lombok.Getter;

/**
 * Domain event raised when a spending limit reaches its warning threshold after an expense is registered.
 */
@Getter
public class SpendingLimitWarningReachedEvent {

    /**
     * Spending limit aggregate that crossed the warning threshold.
     */
    private final SpendingLimit spendingLimit;

    /**
     * Creates a warning event for a threshold-reaching spending limit.
     *
     * @param spendingLimit aggregate that reached WARNING status
     */
    public SpendingLimitWarningReachedEvent(SpendingLimit spendingLimit) {
        this.spendingLimit = spendingLimit;
    }
}

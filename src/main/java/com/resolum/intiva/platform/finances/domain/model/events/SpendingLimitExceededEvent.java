package com.resolum.intiva.platform.finances.domain.model.events;

import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import lombok.Getter;

/**
 * Domain event raised when a spending limit is exceeded after an expense is registered.
 */
@Getter
public class SpendingLimitExceededEvent {

    /**
     * Spending limit aggregate that became exceeded.
     */
    private final SpendingLimit spendingLimit;

    /**
     * Creates an exceeded event for a spending limit aggregate.
     *
     * @param spendingLimit aggregate that reached EXCEEDED status
     */
    public SpendingLimitExceededEvent(SpendingLimit spendingLimit) {
        this.spendingLimit = spendingLimit;
    }
}

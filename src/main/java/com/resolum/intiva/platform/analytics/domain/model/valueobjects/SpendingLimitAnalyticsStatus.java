package com.resolum.intiva.platform.analytics.domain.model.valueobjects;

/**
 * Represents the health status of a spending limit from an analytics perspective.
 * <p>The status is derived from the usage percentage of the limit.</p>
 */
public enum SpendingLimitAnalyticsStatus {
    /**
     * Usage is below 80% of the configured limit.
     */
    SAFE,

    /**
     * Usage is at or above 80% but below 100% of the configured limit.
     */
    WARNING,

    /**
     * Usage meets or exceeds 100% of the configured limit.
     */
    EXCEEDED
}

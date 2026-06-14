package com.resolum.intiva.platform.shared.domain.valueobjects;

/**
 * Represents the types of time periods used for financial analytics and recurring operations.
 * <p>Each constant defines a granularity for aggregating or scheduling financial data.</p>
 */
public enum PeriodTypes {
    /**
     * Daily period — covers a single calendar day.
     */
    DAILY,

    /**
     * Weekly period — covers one ISO week (Monday through Sunday).
     */
    WEEKLY,

    /**
     * Monthly period — covers one calendar month.
     */
    MONTHLY,

    /**
     * Annual period — covers one calendar year.
     */
    ANNUAL
}

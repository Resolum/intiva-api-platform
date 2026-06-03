package com.resolum.intiva.platform.finances.domain.model.valueobjects;

/**
 * Supported execution cadences for recurring transactions.
 *
 * <p>The values intentionally cover the current product scope discussed for finances:
 * weekly, biweekly and monthly recurring incomes or expenses.</p>
 */
public enum RecurringFrequency {
    WEEKLY,
    BIWEEKLY,
    MONTHLY
}

package com.resolum.intiva.platform.analytics.domain.model.valueobjects;

import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * Value object representing a time period for analytics aggregation.
 *
 * @param periodType the granularity of the period
 * @param start      inclusive start date
 * @param end        inclusive end date
 */
public record AnalyticsPeriod(PeriodTypes periodType, LocalDate start, LocalDate end) {

    /**
     * Creates an {@link AnalyticsPeriod} for the current time period based on today's date.
     *
     * @param periodType the desired granularity (DAILY, WEEKLY, MONTHLY, or ANNUAL)
     * @return a period covering the current day, week, month, or year
     */
    public static AnalyticsPeriod current(PeriodTypes periodType) {
        var today = LocalDate.now();
        return switch (periodType) {
            case DAILY -> new AnalyticsPeriod(PeriodTypes.DAILY, today, today);
            case WEEKLY -> {
                var weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                var weekEnd = weekStart.plusDays(6);
                yield new AnalyticsPeriod(PeriodTypes.WEEKLY, weekStart, weekEnd);
            }
            case MONTHLY -> {
                var monthStart = today.withDayOfMonth(1);
                var monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
                yield new AnalyticsPeriod(PeriodTypes.MONTHLY, monthStart, monthEnd);
            }
            case ANNUAL -> {
                var yearStart = today.withDayOfYear(1);
                var yearEnd = yearStart.withDayOfYear(yearStart.lengthOfYear());
                yield new AnalyticsPeriod(PeriodTypes.ANNUAL, yearStart, yearEnd);
            }
        };
    }

    /**
     * Creates an {@link AnalyticsPeriod} for the previous time period relative to today.
     *
     * @param periodType the desired granularity (DAILY, WEEKLY, MONTHLY, or ANNUAL)
     * @return a period covering the previous day, week, month, or year
     */
    public static AnalyticsPeriod previous(PeriodTypes periodType) {
        var today = LocalDate.now();
        return switch (periodType) {
            case DAILY -> new AnalyticsPeriod(PeriodTypes.DAILY, today.minusDays(1), today.minusDays(1));
            case WEEKLY -> {
                var weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
                var weekEnd = weekStart.plusDays(6);
                yield new AnalyticsPeriod(PeriodTypes.WEEKLY, weekStart, weekEnd);
            }
            case MONTHLY -> {
                var previousMonth = today.minusMonths(1);
                var monthStart = previousMonth.withDayOfMonth(1);
                var monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
                yield new AnalyticsPeriod(PeriodTypes.MONTHLY, monthStart, monthEnd);
            }
            case ANNUAL -> {
                var previousYear = today.minusYears(1);
                var yearStart = previousYear.withDayOfYear(1);
                var yearEnd = yearStart.withDayOfYear(yearStart.lengthOfYear());
                yield new AnalyticsPeriod(PeriodTypes.ANNUAL, yearStart, yearEnd);
            }
        };
    }
}

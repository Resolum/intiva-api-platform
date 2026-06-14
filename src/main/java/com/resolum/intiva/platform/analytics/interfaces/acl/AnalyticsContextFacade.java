package com.resolum.intiva.platform.analytics.interfaces.acl;

import com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsSummary;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SavingGoalAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SpendingLimitAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.AnalyticsPeriodTrend;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.CategoryExpenseSummary;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;

import java.time.LocalDate;
import java.util.List;

/**
 * Facade interface that exposes analytics capabilities to other bounded contexts.
 * Provides methods for retrieving analytics summaries, spending limit analytics,
 * saving goal analytics, category expense rankings, and income vs expense trends.
 */
public interface AnalyticsContextFacade {

    /**
     * Retrieves the analytics summary for a given owner and period.
     * @param ownerId the unique identifier of the owner
     * @param ownerType the type of owner (e.g., INDIVIDUAL, FAMILY)
     * @param periodType the type of period (e.g., DAILY, WEEKLY, MONTHLY, ANNUAL)
     * @param periodStart the start date of the period
     * @param periodEnd the end date of the period
     * @return a fully populated AnalyticsSummary with income, expenses, net balance, and category breakdown
     */
    AnalyticsSummary getAnalyticsSummary(String ownerId, OwnerTypes ownerType, PeriodTypes periodType, LocalDate periodStart, LocalDate periodEnd);

    /**
     * Retrieves the spending limit analytics for a given owner and period type.
     * @param ownerId the unique identifier of the owner
     * @param ownerType the type of owner (e.g., INDIVIDUAL, FAMILY)
     * @param periodType the type of period for which spending limits are evaluated
     * @return a SpendingLimitAnalytics with total limits, exceeded, warning, and safe counts
     */
    SpendingLimitAnalytics getSpendingLimitAnalytics(String ownerId, OwnerTypes ownerType, PeriodTypes periodType);

    /**
     * Retrieves the saving goal analytics for a given owner.
     * @param ownerId the unique identifier of the owner
     * @param ownerType the type of owner (e.g., INDIVIDUAL, FAMILY)
     * @return a SavingGoalAnalytics with completion rates, overall progress, and per-goal breakdown
     */
    SavingGoalAnalytics getSavingGoalAnalytics(String ownerId, OwnerTypes ownerType);

    /**
     * Retrieves the ranking of expense categories for a given owner and period.
     * @param ownerId the unique identifier of the owner
     * @param ownerType the type of owner (e.g., INDIVIDUAL, FAMILY)
     * @param periodType the type of period for the ranking
     * @param periodStart the start date of the period
     * @param periodEnd the end date of the period
     * @param limit the maximum number of categories to return
     * @return a ranked list of CategoryExpenseSummary ordered by total amount descending
     */
    List<CategoryExpenseSummary> getCategoryExpenseRanking(String ownerId, OwnerTypes ownerType, PeriodTypes periodType, LocalDate periodStart, LocalDate periodEnd, Integer limit);

    /**
     * Retrieves the income vs expense trend for the last N periods.
     * @param ownerId the unique identifier of the owner
     * @param ownerType the type of owner (e.g., INDIVIDUAL, FAMILY)
     * @param periodType the type of period for the trend
     * @param lastNPeriods the number of periods to look back from the current date
     * @return a chronological list of AnalyticsPeriodTrend with income, expenses, and net balance per period
     */
    List<AnalyticsPeriodTrend> getIncomeVsExpenseTrend(String ownerId, OwnerTypes ownerType, PeriodTypes periodType, Integer lastNPeriods);
}

package com.resolum.intiva.platform.analytics.domain.services;

import com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsSummary;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SavingGoalAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SpendingLimitAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.AnalyticsPeriodTrend;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.CategoryExpenseSummary;
import com.resolum.intiva.platform.analytics.domain.model.queries.*;

import java.util.List;

/**
 * Application-facing query contract for the analytics bounded context.
 *
 * <p>All analytics are computed on the fly from the finances and savings bounded contexts
 * through the ACL layer. No dedicated analytics store is used.</p>
 */
public interface AnalyticsQueryService {

    /**
     * Computes a financial summary for the given owner and period.
     *
     * @param query parameters identifying the owner, period, and scope
     * @return the computed analytics summary
     */
    AnalyticsSummary handle(GetAnalyticsSummaryByOwnerQuery query);

    /**
     * Computes spending limit analytics for the given owner.
     *
     * @param query parameters identifying the owner and scope
     * @return the computed spending limit analytics
     */
    SpendingLimitAnalytics handle(GetSpendingLimitAnalyticsByOwnerQuery query);

    /**
     * Computes saving goal analytics for the given owner.
     *
     * @param query parameters identifying the owner and scope
     * @return the computed saving goal analytics
     */
    SavingGoalAnalytics handle(GetSavingGoalAnalyticsByOwnerQuery query);

    /**
     * Computes a ranking of the top N expense categories for the given owner and period.
     *
     * @param query parameters identifying the owner, period, and limit
     * @return a ranked list of category expense summaries
     */
    List<CategoryExpenseSummary> handle(GetCategoryExpenseRankingQuery query);

    /**
     * Computes the income vs expense trend for the last N periods.
     *
     * @param query parameters identifying the owner, period type, and number of periods
     * @return a chronological list of period trends
     */
    List<AnalyticsPeriodTrend> handle(GetIncomeVsExpenseTrendQuery query);
}

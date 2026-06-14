package com.resolum.intiva.platform.analytics.application.acl.services;

import com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsSummary;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SavingGoalAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SpendingLimitAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.queries.*;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.AnalyticsPeriodTrend;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.CategoryExpenseSummary;
import com.resolum.intiva.platform.analytics.interfaces.acl.AnalyticsContextFacade;
import com.resolum.intiva.platform.analytics.domain.services.AnalyticsQueryService;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of the {@link AnalyticsContextFacade} interface that serves as the Anti-Corruption Layer (ACL)
 * for the analytics bounded context. This service delegates analytics queries to the {@link AnalyticsQueryService}
 * and acts as a facade for other bounded contexts to consume analytics data without coupling to the
 * internal domain model.
 */
@Slf4j
@Service
public class AnalyticsContextFacadeImpl implements AnalyticsContextFacade {

    /**
     * Service used to handle analytics queries for summaries, spending limits, saving goals, and trends.
     */
    private final AnalyticsQueryService analyticsQueryService;

    /**
     * Creates the analytics context facade with its required query service dependency.
     * @param analyticsQueryService the analytics query service used to handle analytics queries
     */
    public AnalyticsContextFacadeImpl(AnalyticsQueryService analyticsQueryService) {
        this.analyticsQueryService = analyticsQueryService;
    }

    /**
     * Retrieves the analytics summary for a given owner and period by delegating to the query service.
     * @param ownerId the unique identifier of the owner
     * @param ownerType the type of owner (e.g., INDIVIDUAL, FAMILY)
     * @param periodType the type of period (e.g., DAILY, WEEKLY, MONTHLY, ANNUAL)
     * @param periodStart the start date of the period
     * @param periodEnd the end date of the period
     * @return a fully populated AnalyticsSummary with income, expenses, net balance, and category breakdown
     */
    @Override
    public AnalyticsSummary getAnalyticsSummary(String ownerId, OwnerTypes ownerType, PeriodTypes periodType, LocalDate periodStart, LocalDate periodEnd) {
        log.info("ACL - Fetching analytics summary for ownerId={}, ownerType={}, period=[{}, {}]", ownerId, ownerType, periodStart, periodEnd);
        var query = new GetAnalyticsSummaryByOwnerQuery(ownerId, ownerType, periodType, periodStart, periodEnd);
        return analyticsQueryService.handle(query);
    }

    /**
     * Retrieves the spending limit analytics for a given owner and period type.
     * @param ownerId the unique identifier of the owner
     * @param ownerType the type of owner (e.g., INDIVIDUAL, FAMILY)
     * @param periodType the type of period for which spending limits are evaluated
     * @return a SpendingLimitAnalytics with total limits, exceeded, warning, and safe counts
     */
    @Override
    public SpendingLimitAnalytics getSpendingLimitAnalytics(String ownerId, OwnerTypes ownerType, PeriodTypes periodType) {
        log.info("ACL - Fetching spending limit analytics for ownerId={}, ownerType={}", ownerId, ownerType);
        var query = new GetSpendingLimitAnalyticsByOwnerQuery(ownerId, ownerType, periodType);
        return analyticsQueryService.handle(query);
    }

    /**
     * Retrieves the saving goal analytics for a given owner.
     * @param ownerId the unique identifier of the owner
     * @param ownerType the type of owner (e.g., INDIVIDUAL, FAMILY)
     * @return a SavingGoalAnalytics with completion rates, overall progress, and per-goal breakdown
     */
    @Override
    public SavingGoalAnalytics getSavingGoalAnalytics(String ownerId, OwnerTypes ownerType) {
        log.info("ACL - Fetching saving goal analytics for ownerId={}, ownerType={}", ownerId, ownerType);
        var query = new GetSavingGoalAnalyticsByOwnerQuery(ownerId, ownerType);
        return analyticsQueryService.handle(query);
    }

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
    @Override
    public List<CategoryExpenseSummary> getCategoryExpenseRanking(String ownerId, OwnerTypes ownerType, PeriodTypes periodType, LocalDate periodStart, LocalDate periodEnd, Integer limit) {
        log.info("ACL - Fetching category expense ranking for ownerId={}, ownerType={}, limit={}", ownerId, ownerType, limit);
        var query = new GetCategoryExpenseRankingQuery(ownerId, ownerType, periodType, periodStart, periodEnd, limit);
        return analyticsQueryService.handle(query);
    }

    /**
     * Retrieves the income vs expense trend for the last N periods.
     * @param ownerId the unique identifier of the owner
     * @param ownerType the type of owner (e.g., INDIVIDUAL, FAMILY)
     * @param periodType the type of period for the trend
     * @param lastNPeriods the number of periods to look back from the current date
     * @return a chronological list of AnalyticsPeriodTrend with income, expenses, and net balance per period
     */
    @Override
    public List<AnalyticsPeriodTrend> getIncomeVsExpenseTrend(String ownerId, OwnerTypes ownerType, PeriodTypes periodType, Integer lastNPeriods) {
        log.info("ACL - Fetching income vs expense trend for ownerId={}, ownerType={}, periods={}", ownerId, ownerType, lastNPeriods);
        var query = new GetIncomeVsExpenseTrendQuery(ownerId, ownerType, periodType, lastNPeriods);
        return analyticsQueryService.handle(query);
    }
}

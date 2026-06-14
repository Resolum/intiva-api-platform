package com.resolum.intiva.platform.analytics.domain.services;

import com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsSummary;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SavingGoalAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SpendingLimitAnalytics;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;

import java.time.LocalDate;

/**
 * Domain port that defines the contract for caching analytics aggregates.
 *
 * <p>This interface follows the hexagonal architecture port-adapter pattern.
 * The domain layer depends on this port abstraction, while the infrastructure
 * layer provides a concrete implementation (e.g., Redis).</p>
 *
 * <p>Implementations are responsible for storing, retrieving, and evicting
 * computed analytics for financial summaries, spending limit analytics,
 * and saving goal analytics.</p>
 */
public interface AnalyticsCachePort {

    /**
     * Caches a computed financial summary.
     *
     * @param summary the analytics summary to cache
     */
    void saveAnalyticsSummary(AnalyticsSummary summary);

    /**
     * Retrieves a cached financial summary by its owner and period parameters.
     *
     * @param ownerId    owner identifier
     * @param ownerType  owner scope
     * @param periodType period granularity
     * @param periodStart period start date
     * @param periodEnd   period end date
     * @return the cached AnalyticsSummary, or null if not found
     */
    AnalyticsSummary findAnalyticsSummary(
            String ownerId,
            OwnerTypes ownerType,
            PeriodTypes periodType,
            LocalDate periodStart,
            LocalDate periodEnd
    );

    /**
     * Caches computed spending limit analytics.
     *
     * @param analytics the spending limit analytics to cache
     */
    void saveSpendingLimitAnalytics(SpendingLimitAnalytics analytics);

    /**
     * Retrieves cached spending limit analytics by its owner and period type.
     *
     * @param ownerId    owner identifier
     * @param ownerType  owner scope
     * @param periodType period granularity
     * @return the cached SpendingLimitAnalytics, or null if not found
     */
    SpendingLimitAnalytics findSpendingLimitAnalytics(
            String ownerId,
            OwnerTypes ownerType,
            PeriodTypes periodType
    );

    /**
     * Caches computed saving goal analytics.
     *
     * @param analytics the saving goal analytics to cache
     */
    void saveSavingGoalAnalytics(SavingGoalAnalytics analytics);

    /**
     * Retrieves cached saving goal analytics by its owner.
     *
     * @param ownerId   owner identifier
     * @param ownerType owner scope
     * @return the cached SavingGoalAnalytics, or null if not found
     */
    SavingGoalAnalytics findSavingGoalAnalytics(
            String ownerId,
            OwnerTypes ownerType
    );

    /**
     * Evicts all cached analytics data for the given owner.
     *
     * <p>This method should clear cache entries for all analytics types
     * (summary, spending, saving) associated with the specified owner scope.</p>
     *
     * @param ownerId   owner identifier
     * @param ownerType owner scope
     */
    void evictByOwner(String ownerId, OwnerTypes ownerType);
}

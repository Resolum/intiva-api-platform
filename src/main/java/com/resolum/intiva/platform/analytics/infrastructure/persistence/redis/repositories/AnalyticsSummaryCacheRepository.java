package com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.repositories;

import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.entities.AnalyticsSummaryCacheEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Spring Data Redis repository for {@link AnalyticsSummaryCacheEntity} persistence.
 *
 * <p>This repository provides CRUD operations for cached analytics summaries
 * in Redis. Query methods allow lookups by owner and period parameters,
 * as well as bulk eviction by owner scope.</p>
 */
public interface AnalyticsSummaryCacheRepository
        extends CrudRepository<AnalyticsSummaryCacheEntity, String> {

    /**
     * Finds a cached analytics summary by its owner and period parameters.
     *
     * @param ownerId    owner identifier
     * @param ownerType  owner scope as a string (INDIVIDUAL or FAMILY)
     * @param periodType period granularity as a string (DAILY, WEEKLY, MONTHLY, ANNUAL)
     * @param periodStart period start date as a string
     * @param periodEnd   period end date as a string
     * @return an Optional containing the cache entity if found, or empty if not found
     */
    Optional<AnalyticsSummaryCacheEntity> findByOwnerIdAndOwnerTypeAndPeriodTypeAndPeriodStartAndPeriodEnd(
            String ownerId,
            String ownerType,
            String periodType,
            String periodStart,
            String periodEnd
    );

    /**
     * Deletes all cached analytics summaries for the given owner scope.
     *
     * @param ownerId   owner identifier
     * @param ownerType owner scope as a string (INDIVIDUAL or FAMILY)
     */
    void deleteAllByOwnerIdAndOwnerType(String ownerId, String ownerType);
}

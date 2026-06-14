package com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.repositories;

import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.entities.SpendingLimitAnalyticsCacheEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Spring Data Redis repository for {@link SpendingLimitAnalyticsCacheEntity} persistence.
 *
 * <p>This repository provides CRUD operations for cached spending limit analytics
 * in Redis. Query methods allow lookups by owner and period type, as well as
 * bulk eviction by owner scope.</p>
 */
public interface SpendingLimitAnalyticsCacheRepository
        extends CrudRepository<SpendingLimitAnalyticsCacheEntity, String> {

    /**
     * Finds a cached spending limit analytics entry by its owner and period type.
     *
     * @param ownerId    owner identifier
     * @param ownerType  owner scope as a string (INDIVIDUAL or FAMILY)
     * @param periodType period granularity as a string (DAILY, WEEKLY, MONTHLY, ANNUAL)
     * @return an Optional containing the cache entity if found, or empty if not found
     */
    Optional<SpendingLimitAnalyticsCacheEntity> findByOwnerIdAndOwnerTypeAndPeriodType(
            String ownerId,
            String ownerType,
            String periodType
    );

    /**
     * Deletes all cached spending limit analytics for the given owner scope.
     *
     * @param ownerId   owner identifier
     * @param ownerType owner scope as a string (INDIVIDUAL or FAMILY)
     */
    void deleteAllByOwnerIdAndOwnerType(String ownerId, String ownerType);
}

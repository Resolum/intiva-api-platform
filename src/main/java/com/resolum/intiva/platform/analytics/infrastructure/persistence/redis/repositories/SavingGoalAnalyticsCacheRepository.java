package com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.repositories;

import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.entities.SavingGoalAnalyticsCacheEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Spring Data Redis repository for {@link SavingGoalAnalyticsCacheEntity} persistence.
 *
 * <p>This repository provides CRUD operations for cached saving goal analytics
 * in Redis. Query methods allow lookups by owner, as well as bulk eviction
 * by owner scope.</p>
 */
public interface SavingGoalAnalyticsCacheRepository
        extends CrudRepository<SavingGoalAnalyticsCacheEntity, String> {

    /**
     * Finds a cached saving goal analytics entry by its owner.
     *
     * @param ownerId   owner identifier
     * @param ownerType owner scope as a string (INDIVIDUAL or FAMILY)
     * @return an Optional containing the cache entity if found, or empty if not found
     */
    Optional<SavingGoalAnalyticsCacheEntity> findByOwnerIdAndOwnerType(
            String ownerId,
            String ownerType
    );

    /**
     * Deletes all cached saving goal analytics for the given owner scope.
     *
     * @param ownerId   owner identifier
     * @param ownerType owner scope as a string (INDIVIDUAL or FAMILY)
     */
    void deleteAllByOwnerIdAndOwnerType(String ownerId, String ownerType);
}

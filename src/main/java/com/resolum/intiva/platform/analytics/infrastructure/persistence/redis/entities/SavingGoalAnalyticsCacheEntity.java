package com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.entities;

import com.resolum.intiva.platform.shared.infrastructure.persistence.redis.entities.AbstractCacheEntity;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

/**
 * Redis hash entity that stores a cached {@code SavingGoalAnalytics}.
 *
 * <p>This entity uses {@code analytics:saving} as the Redis key prefix.
 * The full Redis key is constructed as:
 * {@code analytics:saving:{ownerType}:{ownerId}}.</p>
 *
 * <p>Money fields ({@code totalTargetAmount}, {@code totalCurrentAmount}) are stored
 * as byte arrays serialised from the {@code Money} value object.
 * The {@code details} field stores a JSON string representation of the saving goal detail list.</p>
 *
 * <p>Cache entries expire automatically after 15 minutes (default TTL).</p>
 */
@RedisHash("analytics:saving")
public class SavingGoalAnalyticsCacheEntity extends AbstractCacheEntity {

    /**
     * Owner identifier. For INDIVIDUAL this is a user id; for FAMILY this is a group id.
     */
    @Indexed
    private String ownerId;

    /**
     * Owner scope as a string (INDIVIDUAL or FAMILY).
     */
    @Indexed
    private String ownerType;

    /**
     * Total number of saving goals for the owner.
     */
    private Integer totalGoals;

    /**
     * Number of goals marked as COMPLETED.
     */
    private Integer goalsCompleted;

    /**
     * Number of goals currently INPROGRESS.
     */
    private Integer goalsInProgress;

    /**
     * Number of goals marked as UNCOMPLETED.
     */
    private Integer goalsUncompleted;

    /**
     * Sum of target amounts across all goals as serialised Money bytes.
     */
    private byte[] totalTargetAmount;

    /**
     * Sum of current saved amounts across all goals as serialised Money bytes.
     */
    private byte[] totalCurrentAmount;

    /**
     * Overall progress percentage as a BigDecimal string.
     */
    private String overallProgress;

    /**
     * Completion rate as a BigDecimal string (percentage).
     */
    private String completionRate;

    /**
     * Per-goal detail list as a JSON string.
     */
    private String details;

    /**
     * Timestamp when the analytics were generated (Instant string).
     */
    private String generatedAt;

    /**
     * Time-to-live in minutes (default 15). Controls automatic cache expiration.
     */
    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long ttl = 15L;

    /** Returns the owner identifier. */
    public String getOwnerId() { return ownerId; }
    /** Sets the owner identifier. */
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    /** Returns the owner scope as a string. */
    public String getOwnerType() { return ownerType; }
    /** Sets the owner scope as a string. */
    public void setOwnerType(String ownerType) { this.ownerType = ownerType; }

    /** Returns the total number of saving goals. */
    public Integer getTotalGoals() { return totalGoals; }
    /** Sets the total number of saving goals. */
    public void setTotalGoals(Integer totalGoals) { this.totalGoals = totalGoals; }

    /** Returns the number of completed goals. */
    public Integer getGoalsCompleted() { return goalsCompleted; }
    /** Sets the number of completed goals. */
    public void setGoalsCompleted(Integer goalsCompleted) { this.goalsCompleted = goalsCompleted; }

    /** Returns the number of in-progress goals. */
    public Integer getGoalsInProgress() { return goalsInProgress; }
    /** Sets the number of in-progress goals. */
    public void setGoalsInProgress(Integer goalsInProgress) { this.goalsInProgress = goalsInProgress; }

    /** Returns the number of uncompleted goals. */
    public Integer getGoalsUncompleted() { return goalsUncompleted; }
    /** Sets the number of uncompleted goals. */
    public void setGoalsUncompleted(Integer goalsUncompleted) { this.goalsUncompleted = goalsUncompleted; }

    /** Returns the total target amount as serialised Money bytes. */
    public byte[] getTotalTargetAmount() { return totalTargetAmount; }
    /** Sets the total target amount as serialised Money bytes. */
    public void setTotalTargetAmount(byte[] totalTargetAmount) { this.totalTargetAmount = totalTargetAmount; }

    /** Returns the total current amount as serialised Money bytes. */
    public byte[] getTotalCurrentAmount() { return totalCurrentAmount; }
    /** Sets the total current amount as serialised Money bytes. */
    public void setTotalCurrentAmount(byte[] totalCurrentAmount) { this.totalCurrentAmount = totalCurrentAmount; }

    /** Returns the overall progress as a BigDecimal string. */
    public String getOverallProgress() { return overallProgress; }
    /** Sets the overall progress as a BigDecimal string. */
    public void setOverallProgress(String overallProgress) { this.overallProgress = overallProgress; }

    /** Returns the completion rate as a BigDecimal string. */
    public String getCompletionRate() { return completionRate; }
    /** Sets the completion rate as a BigDecimal string. */
    public void setCompletionRate(String completionRate) { this.completionRate = completionRate; }

    /** Returns the per-goal detail list as a JSON string. */
    public String getDetails() { return details; }
    /** Sets the per-goal detail list as a JSON string. */
    public void setDetails(String details) { this.details = details; }

    /** Returns the generation timestamp as a string. */
    public String getGeneratedAt() { return generatedAt; }
    /** Sets the generation timestamp as a string. */
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }

    /** Returns the TTL in minutes. */
    public Long getTtl() { return ttl; }
    /** Sets the TTL in minutes. */
    public void setTtl(Long ttl) { this.ttl = ttl; }
}

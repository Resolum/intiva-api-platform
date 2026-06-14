package com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.entities;

import com.resolum.intiva.platform.shared.infrastructure.persistence.redis.entities.AbstractCacheEntity;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

/**
 * Redis hash entity that stores a cached {@code SpendingLimitAnalytics}.
 *
 * <p>This entity uses {@code analytics:spending} as the Redis key prefix.
 * The full Redis key is constructed as:
 * {@code analytics:spending:{ownerType}:{ownerId}:{periodType}}.</p>
 *
 * <p>Numeric fields track the count of limits in each state (safe, warning, exceeded).
 * The {@code details} field stores a JSON string representation of the spending limit detail list.</p>
 *
 * <p>Cache entries expire automatically after 15 minutes (default TTL).</p>
 */
@RedisHash("analytics:spending")
public class SpendingLimitAnalyticsCacheEntity extends AbstractCacheEntity {

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
     * Period granularity as a string (DAILY, WEEKLY, MONTHLY, ANNUAL).
     */
    private String periodType;

    /**
     * Total number of spending limits configured for the owner.
     */
    private Integer totalLimitsSet;

    /**
     * Number of limits where the spent amount meets or exceeds the configured limit.
     */
    private Integer limitsExceeded;

    /**
     * Number of limits where usage is at or above 80% but below 100%.
     */
    private Integer limitsAtWarning;

    /**
     * Number of limits where usage is below 80%.
     */
    private Integer limitsSafe;

    /**
     * Exceeded rate as a BigDecimal string (percentage).
     */
    private String exceededRate;

    /**
     * Warning rate as a BigDecimal string (percentage).
     */
    private String warningRate;

    /**
     * Per-limit detail list as a JSON string.
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

    /** Returns the period granularity as a string. */
    public String getPeriodType() { return periodType; }
    /** Sets the period granularity as a string. */
    public void setPeriodType(String periodType) { this.periodType = periodType; }

    /** Returns the total number of spending limits set. */
    public Integer getTotalLimitsSet() { return totalLimitsSet; }
    /** Sets the total number of spending limits set. */
    public void setTotalLimitsSet(Integer totalLimitsSet) { this.totalLimitsSet = totalLimitsSet; }

    /** Returns the number of exceeded limits. */
    public Integer getLimitsExceeded() { return limitsExceeded; }
    /** Sets the number of exceeded limits. */
    public void setLimitsExceeded(Integer limitsExceeded) { this.limitsExceeded = limitsExceeded; }

    /** Returns the number of limits in warning state. */
    public Integer getLimitsAtWarning() { return limitsAtWarning; }
    /** Sets the number of limits in warning state. */
    public void setLimitsAtWarning(Integer limitsAtWarning) { this.limitsAtWarning = limitsAtWarning; }

    /** Returns the number of safe limits. */
    public Integer getLimitsSafe() { return limitsSafe; }
    /** Sets the number of safe limits. */
    public void setLimitsSafe(Integer limitsSafe) { this.limitsSafe = limitsSafe; }

    /** Returns the exceeded rate as a BigDecimal string. */
    public String getExceededRate() { return exceededRate; }
    /** Sets the exceeded rate as a BigDecimal string. */
    public void setExceededRate(String exceededRate) { this.exceededRate = exceededRate; }

    /** Returns the warning rate as a BigDecimal string. */
    public String getWarningRate() { return warningRate; }
    /** Sets the warning rate as a BigDecimal string. */
    public void setWarningRate(String warningRate) { this.warningRate = warningRate; }

    /** Returns the per-limit detail list as a JSON string. */
    public String getDetails() { return details; }
    /** Sets the per-limit detail list as a JSON string. */
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

package com.resolum.intiva.platform.analytics.domain.model.aggregates;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.SpendingLimitDetail;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

/**
 * Aggregate root that represents computed analytics for spending limits of a specific owner.
 *
 * <p>This aggregate classifies spending limits into SAFE, WARNING, or EXCEEDED categories and
 * provides aggregate metrics such as exceeded rate and warning rate. It is computed on the fly
 * from the spending limits stored in the finances bounded context.</p>
 */
public class SpendingLimitAnalytics {

    /**
     * Unique identifier for this analytics instance.
     */
    private final String id;

    /**
     * Scope that owns the spending limits (INDIVIDUAL or FAMILY).
     */
    private final OwnerTypes ownerType;

    /**
     * Owner identifier. For INDIVIDUAL this is a user id; for FAMILY this is a group id.
     */
    private final String ownerId;

    /**
     * Period granularity for which the analytics were computed.
     */
    private final PeriodTypes periodType;

    /**
     * Total number of spending limits set for the owner.
     */
    private final Integer totalLimitsSet;

    /**
     * Number of limits where the spent amount meets or exceeds the configured limit.
     */
    private final Integer limitsExceeded;

    /**
     * Number of limits where usage is at or above 80% but below 100%.
     */
    private final Integer limitsAtWarning;

    /**
     * Number of limits where usage is below 80%.
     */
    private final Integer limitsSafe;

    /**
     * Detailed breakdown of each spending limit and its current status.
     */
    private final List<SpendingLimitDetail> details;

    /**
     * Timestamp when this analytics was generated.
     */
    private final Instant generatedAt;

    /**
     * Creates spending limit analytics with all computed values.
     *
     * @param id              unique instance identifier
     * @param ownerType       owner scope
     * @param ownerId         owner identifier
     * @param periodType      period granularity
     * @param totalLimitsSet  total number of limits
     * @param limitsExceeded  number of exceeded limits
     * @param limitsAtWarning number of limits in warning state
     * @param limitsSafe      number of safe limits
     * @param details         per-limit details
     * @param generatedAt     generation timestamp
     */
    public SpendingLimitAnalytics(String id, OwnerTypes ownerType, String ownerId, PeriodTypes periodType,
                                  Integer totalLimitsSet, Integer limitsExceeded, Integer limitsAtWarning,
                                  Integer limitsSafe, List<SpendingLimitDetail> details,
                                  Instant generatedAt) {
        this.id = id;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.periodType = periodType;
        this.totalLimitsSet = totalLimitsSet;
        this.limitsExceeded = limitsExceeded;
        this.limitsAtWarning = limitsAtWarning;
        this.limitsSafe = limitsSafe;
        this.details = details;
        this.generatedAt = generatedAt;
    }

    /**
     * Returns the unique identifier.
     *
     * @return analytics id
     */
    public String getId() { return id; }

    /**
     * Returns the owner scope.
     *
     * @return owner type
     */
    public OwnerTypes getOwnerType() { return ownerType; }

    /**
     * Returns the owner identifier.
     *
     * @return owner id
     */
    public String getOwnerId() { return ownerId; }

    /**
     * Returns the period granularity.
     *
     * @return period type
     */
    public PeriodTypes getPeriodType() { return periodType; }

    /**
     * Returns the total number of spending limits.
     *
     * @return total limits
     */
    public Integer getTotalLimitsSet() { return totalLimitsSet; }

    /**
     * Returns the number of exceeded limits.
     *
     * @return exceeded limit count
     */
    public Integer getLimitsExceeded() { return limitsExceeded; }

    /**
     * Returns the number of limits in warning state.
     *
     * @return warning limit count
     */
    public Integer getLimitsAtWarning() { return limitsAtWarning; }

    /**
     * Returns the number of safe limits.
     *
     * @return safe limit count
     */
    public Integer getLimitsSafe() { return limitsSafe; }

    /**
     * Returns the detailed breakdown per spending limit.
     *
     * @return list of limit details
     */
    public List<SpendingLimitDetail> getDetails() { return details; }

    /**
     * Returns the generation timestamp.
     *
     * @return generation timestamp
     */
    public Instant getGeneratedAt() { return generatedAt; }

    /**
     * Calculates the percentage of limits that are exceeded.
     * <p>Returns 0.00 when there are no limits set to avoid division by zero.</p>
     *
     * @return exceeded rate percentage with 2 decimal places
     */
    public BigDecimal exceededRate() {
        if (totalLimitsSet == null || totalLimitsSet == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(limitsExceeded)
                .divide(BigDecimal.valueOf(totalLimitsSet), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the percentage of limits that are in warning state.
     * <p>Returns 0.00 when there are no limits set to avoid division by zero.</p>
     *
     * @return warning rate percentage with 2 decimal places
     */
    public BigDecimal warningRate() {
        if (totalLimitsSet == null || totalLimitsSet == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(limitsAtWarning)
                .divide(BigDecimal.valueOf(totalLimitsSet), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}

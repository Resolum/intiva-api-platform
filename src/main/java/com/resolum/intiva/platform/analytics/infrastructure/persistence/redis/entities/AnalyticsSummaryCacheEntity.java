package com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.entities;

import com.resolum.intiva.platform.shared.infrastructure.persistence.redis.entities.AbstractCacheEntity;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

/**
 * Redis hash entity that stores a cached {@code AnalyticsSummary}.
 *
 * <p>This entity uses {@code analytics:summary} as the Redis key prefix.
 * The full Redis key is constructed as:
 * {@code analytics:summary:{ownerType}:{ownerId}:{periodType}:{periodStart}:{periodEnd}}.</p>
 *
 * <p>Money fields ({@code totalIncome}, {@code totalExpenses}, {@code netBalance}) are stored
 * as byte arrays serialised from the {@code Money} value object.
 * The {@code expensesByCategory} field stores a JSON string representation of the category list.</p>
 *
 * <p>Cache entries expire automatically after 15 minutes (default TTL).</p>
 */
@RedisHash("analytics:summary")
public class AnalyticsSummaryCacheEntity extends AbstractCacheEntity {

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
     * Period start date as a string (ISO-8601 format).
     */
    private String periodStart;

    /**
     * Period end date as a string (ISO-8601 format).
     */
    private String periodEnd;

    /**
     * Total income as serialised Money bytes.
     */
    private byte[] totalIncome;

    /**
     * Total expenses as serialised Money bytes.
     */
    private byte[] totalExpenses;

    /**
     * Net balance as serialised Money bytes.
     */
    private byte[] netBalance;

    /**
     * Savings rate as a BigDecimal string (percentage).
     */
    private String savingsRate;

    /**
     * Expense breakdown by category as a JSON string.
     */
    private String expensesByCategory;

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

    /** Returns the period start date as a string. */
    public String getPeriodStart() { return periodStart; }
    /** Sets the period start date as a string. */
    public void setPeriodStart(String periodStart) { this.periodStart = periodStart; }

    /** Returns the period end date as a string. */
    public String getPeriodEnd() { return periodEnd; }
    /** Sets the period end date as a string. */
    public void setPeriodEnd(String periodEnd) { this.periodEnd = periodEnd; }

    /** Returns the total income as serialised Money bytes. */
    public byte[] getTotalIncome() { return totalIncome; }
    /** Sets the total income as serialised Money bytes. */
    public void setTotalIncome(byte[] totalIncome) { this.totalIncome = totalIncome; }

    /** Returns the total expenses as serialised Money bytes. */
    public byte[] getTotalExpenses() { return totalExpenses; }
    /** Sets the total expenses as serialised Money bytes. */
    public void setTotalExpenses(byte[] totalExpenses) { this.totalExpenses = totalExpenses; }

    /** Returns the net balance as serialised Money bytes. */
    public byte[] getNetBalance() { return netBalance; }
    /** Sets the net balance as serialised Money bytes. */
    public void setNetBalance(byte[] netBalance) { this.netBalance = netBalance; }

    /** Returns the savings rate as a BigDecimal string. */
    public String getSavingsRate() { return savingsRate; }
    /** Sets the savings rate as a BigDecimal string. */
    public void setSavingsRate(String savingsRate) { this.savingsRate = savingsRate; }

    /** Returns the expense breakdown by category as a JSON string. */
    public String getExpensesByCategory() { return expensesByCategory; }
    /** Sets the expense breakdown by category as a JSON string. */
    public void setExpensesByCategory(String expensesByCategory) { this.expensesByCategory = expensesByCategory; }

    /** Returns the generation timestamp as a string. */
    public String getGeneratedAt() { return generatedAt; }
    /** Sets the generation timestamp as a string. */
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }

    /** Returns the TTL in minutes. */
    public Long getTtl() { return ttl; }
    /** Sets the TTL in minutes. */
    public void setTtl(Long ttl) { this.ttl = ttl; }
}

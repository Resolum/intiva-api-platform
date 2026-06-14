package com.resolum.intiva.platform.analytics.domain.model.aggregates;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.SavingGoalDetail;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

/**
 * Aggregate root that represents computed analytics for saving goals of a specific owner.
 *
 * <p>This aggregate aggregates saving goals by status (COMPLETED, INPROGRESS, UNCOMPLETED) and
 * calculates overall progress and completion rate. It is computed on the fly from the saving
 * goals stored in the savings bounded context.</p>
 */
public class SavingGoalAnalytics extends AuditableAbstractAggregate<SavingGoalAnalytics> {

    /**
     * Scope that owns the saving goals (INDIVIDUAL or FAMILY).
     */
    private final OwnerTypes ownerType;

    /**
     * Owner identifier. For INDIVIDUAL this is a user id; for FAMILY this is a group id.
     */
    private final String ownerId;

    /**
     * Total number of saving goals for the owner.
     */
    private final Integer totalGoals;

    /**
     * Number of goals marked as COMPLETED.
     */
    private final Integer goalsCompleted;

    /**
     * Number of goals currently INPROGRESS.
     */
    private final Integer goalsInProgress;

    /**
     * Number of goals marked as UNCOMPLETED.
     */
    private final Integer goalsUncompleted;

    /**
     * Sum of target amounts across all goals.
     */
    private final Money totalTargetAmount;

    /**
     * Sum of current saved amounts across all goals.
     */
    private final Money totalCurrentAmount;

    /**
     * Overall progress calculated as (totalCurrentAmount / totalTargetAmount) * 100.
     */
    private final BigDecimal overallProgress;

    /**
     * Detailed breakdown of each saving goal and its current status.
     */
    private final List<SavingGoalDetail> details;

    /**
     * Creates saving goal analytics accepting a legacy string identifier and generation timestamp.
     * <p>The {@code id} and {@code generatedAt} parameters are accepted for backward compatibility
     * with deserialization paths; they are ignored in favour of the inherited identity and creation
     * timestamp.</p>
     *
     * @param id                 legacy instance identifier (ignored)
     * @param ownerType          owner scope
     * @param ownerId            owner identifier
     * @param totalGoals         total number of goals
     * @param goalsCompleted     number of completed goals
     * @param goalsInProgress    number of in-progress goals
     * @param goalsUncompleted   number of uncompleted goals
     * @param totalTargetAmount  aggregated target amount
     * @param totalCurrentAmount aggregated current amount
     * @param overallProgress    overall progress percentage
     * @param details            per-goal details
     * @param generatedAt        legacy generation timestamp (ignored)
     */
    public SavingGoalAnalytics(String id, OwnerTypes ownerType, String ownerId,
                               Integer totalGoals, Integer goalsCompleted, Integer goalsInProgress,
                               Integer goalsUncompleted, Money totalTargetAmount, Money totalCurrentAmount,
                               BigDecimal overallProgress, List<SavingGoalDetail> details, Instant generatedAt) {
        this(ownerType, ownerId, totalGoals, goalsCompleted, goalsInProgress, goalsUncompleted,
                totalTargetAmount, totalCurrentAmount, overallProgress, details);
    }

    /**
     * Creates saving goal analytics with all computed values.
     *
     * @param ownerType          owner scope
     * @param ownerId            owner identifier
     * @param totalGoals         total number of goals
     * @param goalsCompleted     number of completed goals
     * @param goalsInProgress    number of in-progress goals
     * @param goalsUncompleted   number of uncompleted goals
     * @param totalTargetAmount  aggregated target amount
     * @param totalCurrentAmount aggregated current amount
     * @param overallProgress    overall progress percentage
     * @param details            per-goal details
     */
    public SavingGoalAnalytics(OwnerTypes ownerType, String ownerId,
                               Integer totalGoals, Integer goalsCompleted, Integer goalsInProgress,
                               Integer goalsUncompleted, Money totalTargetAmount, Money totalCurrentAmount,
                               BigDecimal overallProgress, List<SavingGoalDetail> details) {
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.totalGoals = totalGoals;
        this.goalsCompleted = goalsCompleted;
        this.goalsInProgress = goalsInProgress;
        this.goalsUncompleted = goalsUncompleted;
        this.totalTargetAmount = totalTargetAmount;
        this.totalCurrentAmount = totalCurrentAmount;
        this.overallProgress = overallProgress;
        this.details = details;
    }

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
     * Returns the total number of saving goals.
     *
     * @return total goals
     */
    public Integer getTotalGoals() { return totalGoals; }

    /**
     * Returns the number of completed goals.
     *
     * @return completed goal count
     */
    public Integer getGoalsCompleted() { return goalsCompleted; }

    /**
     * Returns the number of in-progress goals.
     *
     * @return in-progress goal count
     */
    public Integer getGoalsInProgress() { return goalsInProgress; }

    /**
     * Returns the number of uncompleted goals.
     *
     * @return uncompleted goal count
     */
    public Integer getGoalsUncompleted() { return goalsUncompleted; }

    /**
     * Returns the aggregated target amount across all goals.
     *
     * @return total target amount
     */
    public Money getTotalTargetAmount() { return totalTargetAmount; }

    /**
     * Returns the aggregated current saved amount across all goals.
     *
     * @return total current amount
     */
    public Money getTotalCurrentAmount() { return totalCurrentAmount; }

    /**
     * Returns the overall progress percentage.
     *
     * @return overall progress
     */
    public BigDecimal getOverallProgress() { return overallProgress; }

    /**
     * Returns the detailed breakdown per saving goal.
     *
     * @return list of goal details
     */
    public List<SavingGoalDetail> getDetails() { return details; }

    /**
     * Returns the generation timestamp, falling back to the current instant if the
     * aggregate was not persisted.
     *
     * @return generation timestamp
     */
    public Instant getGeneratedAt() {
        var createdAt = getCreatedAt();
        return createdAt != null ? createdAt : Instant.now();
    }

    /**
     * Calculates the completion rate as {@code (goalsCompleted / totalGoals) * 100}.
     * <p>Returns 0.00 when there are no goals to avoid division by zero.</p>
     *
     * @return completion rate percentage with 2 decimal places
     */
    public BigDecimal completionRate() {
        if (totalGoals == null || totalGoals == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(goalsCompleted)
                .divide(BigDecimal.valueOf(totalGoals), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}

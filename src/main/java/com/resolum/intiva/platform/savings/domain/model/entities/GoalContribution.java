package com.resolum.intiva.platform.savings.domain.model.entities;

import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents a monetary contribution made toward a saving goal.
 */
@Entity
@Getter
@Table(name = "goal_contributions")
public class GoalContribution {

    /**
     * The unique identifier for this contribution.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The amount of money contributed.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "currency_code", nullable = false))
    })
    private Money amountContributed;

    /**
     * The ID of the user who made the contribution.
     */
    @Column(name = "contributor_id", nullable = false)
    private Long contributorId;

    /**
     * The timestamp when this contribution was made.
     */
    @Column(name = "contributed_at", nullable = false, updatable = false)
    private Instant contributedAt;

    /**
     * The ID of the saving goal this contribution is applied to.
     */
    @Column(name = "saving_goal_id", nullable = false)
    private Long savingGoalId;

    /**
     * Default protected constructor for JPA.
     */
    protected GoalContribution() {
    }

    /**
     * Constructs a new GoalContribution.
     *
     * @param amountContributed the amount of money contributed
     * @param contributorId     the ID of the user who made the contribution
     * @param savingGoalId      the ID of the saving goal this contribution is applied to
     */
    public GoalContribution(Money amountContributed, Long contributorId, Long savingGoalId) {
        this.amountContributed = amountContributed;
        this.contributorId = contributorId;
        this.savingGoalId = savingGoalId;
        this.contributedAt = Instant.now();
    }
}

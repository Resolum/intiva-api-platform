package com.resolum.intiva.platform.savings.domain.model.aggregates;

import com.resolum.intiva.platform.savings.domain.model.entities.GoalContribution;
import com.resolum.intiva.platform.savings.domain.model.valueobjects.SavingGoalStatus;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a saving goal associated with a user or family group.
 * Tracks financial progress toward a defined target amount.
 */
@Entity
@Getter
@Table(name = "saving_goals")
public class SavingGoal extends AuditableAbstractAggregate<SavingGoal> {

    /**
     * The type of owner. INDIVIDUAL for personal goals, FAMILY for shared group goals.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false)
    private OwnerTypes ownerType;

    /**
     * The ID of the user acting on the goal.
     */
    @Column(name = "user_id")
    private Long actorUserId;

    /**
     * The ID of the group or owner.
     */
    @Column(name = "group_id")
    private String ownerId;

    /**
     * The title of the saving goal.
     */
    @Column(name = "title")
    private String title;

    /**
     * The current saved amount.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "current_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "currency_code"))
    })
    private Money currentAmount;

    /**
     * The target amount to be saved.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "target_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "target_currency_code"))
    })
    private Money targetAmount;

    /**
     * A brief description of the saving goal.
     */
    @Column(name = "description")
    private String description;

    /**
     * The starting date and time.
     */
    @Column(name = "starts_at")
    private Instant startsAt;

    /**
     * The deadline date and time.
     */
    @Column(name = "deadline")
    private Instant deadline;

    /**
     * The status of the saving goal.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SavingGoalStatus status;

    /**
     * The category ID for the goal.
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * The date and time when the saving goal was successfully completed.
     */
    @Column(name = "completed_at")
    private Instant completedAt;

    /**
     * Default protected constructor for JPA.
     */
    protected SavingGoal() {
    }

    /**
     * Constructs a new SavingGoal with the provided details.
     *
     * @param ownerType     the type of the owner
     * @param actorUserId   the ID of the user acting on the goal
     * @param ownerId       the ID of the group or owner
     * @param title         the title of the saving goal
     * @param currentAmount the current saved amount
     * @param targetAmount  the target amount to be saved
     * @param description   the description of the saving goal
     * @param startsAt      the starting date and time
     * @param deadline      the deadline date and time
     * @param categoryId    the ID of the category for the goal
     */
    public SavingGoal(OwnerTypes ownerType, Long actorUserId, String ownerId, String title, Money currentAmount, Money targetAmount, String description, Instant startsAt, Instant deadline, Long categoryId) {
        this.ownerType = ownerType;
        this.actorUserId = actorUserId;
        this.ownerId = ownerId;
        this.title = title;
        this.currentAmount = currentAmount;
        this.targetAmount = targetAmount;
        this.description = description;
        this.startsAt = startsAt;
        this.deadline = deadline;
        this.status = SavingGoalStatus.INPROGRESS;
        this.categoryId = categoryId;
    }

    /**
     * Marks the saving goal as completed.
     * Sets the status to COMPLETED and records the completion timestamp.
     *
     * @throws IllegalStateException if the saving goal is already marked as completed
     */
    public void completes() {
        if (this.status == SavingGoalStatus.COMPLETED) {
            throw new IllegalStateException("Saving goal is already completed");
        }
        this.status = SavingGoalStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    /**
     * Reverts the saving goal to an uncompleted state.
     * Clears the completion timestamp and sets the status to UNCOMPLETED.
     *
     * @throws IllegalStateException if the saving goal is already marked as uncompleted
     */
    public void uncompletes() {
        if (this.status == SavingGoalStatus.UNCOMPLETED) {
            throw new IllegalStateException("Saving goal is already marked as uncompleted");
        }
        this.status = SavingGoalStatus.UNCOMPLETED;
        this.completedAt = null;
    }

    /**
     * Updates the description and title of the saving goal.
     *
     * @param description the new description
     * @param title       the new title
     */
    public void editDescriptionOrTitle(String description, String title) {
        this.description = description;
        this.title = title;
    }

    /**
     * Updates the target amount of the saving goal.
     *
     * @param newTargetAmount the new target amount
     */
    public void editTargetAmount(Money newTargetAmount) {
        this.targetAmount = newTargetAmount;
    }

    /**
     * Registers a monetary contribution to this saving goal.
     * Updates the current saved amount and automatically marks
     * the goal as COMPLETED if the target amount has been reached.
     *
     * @param contribution the contribution to apply to this saving goal
     * @throws IllegalArgumentException if the contribution amount is zero or negative
     */
    public void contribute(GoalContribution contribution) {
        if (contribution.getAmountContributed().amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount contributed must be greater than zero");
        }
        this.currentAmount = this.currentAmount.add(contribution.getAmountContributed());
        
        if (this.currentAmount.amount().compareTo(this.targetAmount.amount()) >= 0) {
            this.completes();
        }
    }
}

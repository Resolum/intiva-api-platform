package com.resolum.intiva.platform.finances.domain.model.aggregates;

import com.resolum.intiva.platform.finances.domain.model.commands.CreateSpendingLimitCommand;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitExceededEvent;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitWarningReachedEvent;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitStatus;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Aggregate root that represents an expense control rule inside the finances bounded context.
 *
 * <p>A spending limit belongs to an owner and can therefore support both application segments:</p>
 * <ul>
 *     <li>{@link OwnerTypes#INDIVIDUAL}: personal finances, where ownerId is a user id.</li>
 *     <li>{@link OwnerTypes#FAMILY}: group/family finances, where ownerId is a group id.</li>
 * </ul>
 *
 * <p>The target identifies what kind of expense consumes the limit:</p>
 * <ul>
 *     <li>{@link SpendingLimitTargetType#CATEGORY}: expenses with the matching categoryId consume it.</li>
 *     <li>{@link SpendingLimitTargetType#FINANCIAL_ACCOUNT}: expenses with the matching financialAccountId consume it.</li>
 * </ul>
 *
 * <p>{@code limitAmount} is the configured ceiling. {@code spentAmount} is the consumed amount and is increased
 * by matching EXPENSE transactions through the command service orchestration.</p>
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "spending_limits")
public class SpendingLimit extends AuditableAbstractAggregate<SpendingLimit> {

    /**
     * Owner identifier. For INDIVIDUAL this is a user id; for FAMILY this is a family or group id.
     */
    @Column(nullable = false)
    private Long ownerId;

    /**
     * Scope that owns the limit.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnerTypes ownerType;

    /**
     * Kind of target controlled by the limit.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpendingLimitTargetType targetType;

    /**
     * Identifier of the controlled category or financial account.
     */
    @Column(nullable = false)
    private Long targetId;

    /**
     * Maximum amount allowed for the configured period.
     */
    @Embedded
    @Valid
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "limit_amount", nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "limit_currency_code", nullable = false))
    })
    private Money limitAmount;

    /**
     * Amount already consumed by matching expenses.
     */
    @Embedded
    @Valid
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "spent_amount", nullable = false)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "spent_currency_code", nullable = false))
    })
    private Money spentAmount;

    /**
     * First date included in the limit period.
     */
    @Column(nullable = false)
    private LocalDate startDate;

    /**
     * Last date included in the limit period.
     */
    @Column(nullable = false)
    private LocalDate endDate;

    /**
     * Whether the limit is active and eligible for consumption.
     */
    @Column(nullable = false)
    private Boolean active;

    /**
     * Current consumption state of the limit.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpendingLimitStatus status;

    public SpendingLimit(CreateSpendingLimitCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        if (command.ownerId() == null || command.ownerType() == null) {
            throw new IllegalArgumentException("Owner information is required");
        }
        if (command.targetType() == null || command.targetId() == null) {
            throw new IllegalArgumentException("Spending limit target is required");
        }
        if (command.limitAmount() == null || command.limitAmount().getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Limit amount must be greater than zero");
        }

        this.ownerId = command.ownerId();
        this.ownerType = command.ownerType();
        this.targetType = command.targetType();
        this.targetId = command.targetId();
        this.limitAmount = command.limitAmount();
        this.spentAmount = new Money(BigDecimal.ZERO, command.limitAmount().currencyCode());
        this.startDate = command.startDate();
        this.endDate = command.endDate();
        this.active = true;
        this.status = SpendingLimitStatus.NORMAL;

        validatePeriod();
    }

    /**
     * Determines whether a transaction should consume this limit.
     *
     * <p>The transaction must match the same owner, owner type, active period and target. Currency filtering is
     * handled by the application service before calling {@link #registerExpense(Money)}.</p>
     *
     * @param ownerId transaction owner id
     * @param ownerType transaction owner type
     * @param categoryId transaction category id
     * @param financialAccountId transaction financial account id
     * @param transactionDate date used to evaluate the limit period
     * @return true when the transaction is in scope for this limit
     */
    public boolean appliesTo(Long ownerId, OwnerTypes ownerType, Long categoryId, Long financialAccountId, LocalDate transactionDate) {
        if (!Boolean.TRUE.equals(active)) return false;
        if (!this.ownerId.equals(ownerId)) return false;
        if (this.ownerType != ownerType) return false;
        if (transactionDate == null || transactionDate.isBefore(startDate) || transactionDate.isAfter(endDate)) return false;

        return switch (targetType) {
            case CATEGORY -> targetId.equals(categoryId);
            case FINANCIAL_ACCOUNT -> targetId.equals(financialAccountId);
        };
    }

    /**
     * Consumes this limit by adding the expense amount to spentAmount and recalculating the status.
     *
     * @param amount amount from a matching EXPENSE transaction
     */
    public void registerExpense(Money amount) {
        if (!Boolean.TRUE.equals(active)) {
            throw new IllegalStateException("Spending limit is inactive");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Expense amount is required");
        }

        var previousStatus = this.status;
        this.spentAmount = this.spentAmount.add(amount);
        updateStatus();
        registerThresholdNotification(previousStatus);
    }

    /**
     * Updates the configured limit amount without changing the currency.
     *
     * @param newLimitAmount new maximum amount for the same currency
     */
    public void updateLimitAmount(Money newLimitAmount) {
        if (newLimitAmount == null || newLimitAmount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Limit amount must be greater than zero");
        }
        if (!newLimitAmount.currencyCode().equals(this.spentAmount.currencyCode())) {
            throw new IllegalArgumentException("Currency code cannot be changed");
        }

        this.limitAmount = newLimitAmount;
        updateStatus();
    }

    /**
     * Updates the inclusive date range in which matching expenses consume this limit.
     *
     * @param startDate first date included in the period
     * @param endDate last date included in the period
     */
    public void updatePeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        validatePeriod();
    }

    /**
     * Activates the limit so matching expenses consume it again.
     */
    public void activate() {
        this.active = true;
        updateStatus();
    }

    /**
     * Deactivates the limit. Inactive limits are ignored when expenses are registered.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Recalculates the status from current consumption.
     *
     * <p>WARNING starts at 80 percent of the configured limit. EXCEEDED starts when spentAmount is equal to or
     * greater than limitAmount.</p>
     */
    private void updateStatus() {
        if (spentAmount.getAmount().compareTo(limitAmount.getAmount()) >= 0) {
            this.status = SpendingLimitStatus.EXCEEDED;
            return;
        }

        var warningThreshold = limitAmount.getAmount().multiply(new BigDecimal("0.80"));
        if (spentAmount.getAmount().compareTo(warningThreshold) >= 0) {
            this.status = SpendingLimitStatus.WARNING;
            return;
        }

        this.status = SpendingLimitStatus.NORMAL;
    }

    /**
     * Validates that the configured period is complete and ordered chronologically.
     */
    private void validatePeriod() {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Spending limit period is required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    /**
     * Registers one domain event when the consumption status crosses a meaningful threshold.
     *
     * @param previousStatus status before applying the latest expense
     */
    private void registerThresholdNotification(SpendingLimitStatus previousStatus) {
        if (previousStatus == this.status) {
            return;
        }
        if (this.status == SpendingLimitStatus.WARNING) {
            addDomainEvent(new SpendingLimitWarningReachedEvent(this));
            return;
        }
        if (this.status == SpendingLimitStatus.EXCEEDED) {
            addDomainEvent(new SpendingLimitExceededEvent(this));
        }
    }
}

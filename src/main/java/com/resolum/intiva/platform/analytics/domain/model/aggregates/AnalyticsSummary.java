package com.resolum.intiva.platform.analytics.domain.model.aggregates;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.CategoryExpenseSummary;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Aggregate root that represents a computed financial summary for a specific owner and period.
 *
 * <p>This aggregate is ephemeral — it is computed on the fly from source transactions and is not
 * persisted in a dedicated analytics store. It provides a snapshot of income, expenses, net balance,
 * and an expense breakdown by category for the configured time window.</p>
 */
public class AnalyticsSummary {

    /**
     * Unique identifier for this analytics summary instance.
     */
    private final String id;

    /**
     * Scope that owns this summary (INDIVIDUAL or FAMILY).
     */
    private final OwnerTypes ownerType;

    /**
     * Owner identifier. For INDIVIDUAL this is a user id; for FAMILY this is a group id.
     */
    private final String ownerId;

    /**
     * Granularity of the period (DAILY, WEEKLY, MONTHLY, ANNUAL).
     */
    private final PeriodTypes periodType;

    /**
     * Inclusive start date of the analysis period.
     */
    private final LocalDate periodStart;

    /**
     * Inclusive end date of the analysis period.
     */
    private final LocalDate periodEnd;

    /**
     * Sum of all income transactions within the period.
     */
    private final Money totalIncome;

    /**
     * Sum of all expense transactions within the period.
     */
    private final Money totalExpenses;

    /**
     * Net balance calculated as totalIncome minus totalExpenses.
     */
    private final Money netBalance;

    /**
     * Expense breakdown grouped by category, sorted by total amount descending.
     */
    private final List<CategoryExpenseSummary> expensesByCategory;

    /**
     * Timestamp when this summary was generated.
     */
    private final Instant generatedAt;

    /**
     * Creates an analytics summary with all computed values.
     *
     * @param id                 unique instance identifier
     * @param ownerType          owner scope
     * @param ownerId            owner identifier
     * @param periodType         period granularity
     * @param periodStart        period start date
     * @param periodEnd          period end date
     * @param totalIncome        aggregated income
     * @param totalExpenses      aggregated expenses
     * @param netBalance         income minus expenses
     * @param expensesByCategory expense breakdown by category
     * @param generatedAt        generation timestamp
     */
    public AnalyticsSummary(String id, OwnerTypes ownerType, String ownerId, PeriodTypes periodType,
                            LocalDate periodStart, LocalDate periodEnd, Money totalIncome,
                            Money totalExpenses, Money netBalance,
                            List<CategoryExpenseSummary> expensesByCategory, Instant generatedAt) {
        this.id = id;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.periodType = periodType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netBalance = netBalance;
        this.expensesByCategory = expensesByCategory;
        this.generatedAt = generatedAt;
    }

    /**
     * Returns the unique identifier of this summary.
     *
     * @return summary id
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
     * Returns the period start date.
     *
     * @return start date
     */
    public LocalDate getPeriodStart() { return periodStart; }

    /**
     * Returns the period end date.
     *
     * @return end date
     */
    public LocalDate getPeriodEnd() { return periodEnd; }

    /**
     * Returns total income for the period.
     *
     * @return total income
     */
    public Money getTotalIncome() { return totalIncome; }

    /**
     * Returns total expenses for the period.
     *
     * @return total expenses
     */
    public Money getTotalExpenses() { return totalExpenses; }

    /**
     * Returns the net balance for the period.
     *
     * @return net balance
     */
    public Money getNetBalance() { return netBalance; }

    /**
     * Returns the expense breakdown grouped by category.
     *
     * @return category expense summaries
     */
    public List<CategoryExpenseSummary> getExpensesByCategory() { return expensesByCategory; }

    /**
     * Returns the generation timestamp.
     *
     * @return generation timestamp
     */
    public Instant getGeneratedAt() { return generatedAt; }

    /**
     * Convenience alias for {@link #getNetBalance()}.
     *
     * @return net balance
     */
    public Money balance() {
        return netBalance;
    }

    /**
     * Returns the category with the highest expense total, or null if there are no categorized expenses.
     *
     * @return the top category expense summary, or null
     */
    public CategoryExpenseSummary topCategory() {
        return expensesByCategory.stream()
                .max(Comparator.comparing(CategoryExpenseSummary::totalAmount,
                        (m1, m2) -> m1.getAmount().compareTo(m2.getAmount())))
                .orElse(null);
    }

    /**
     * Calculates the savings rate as {@code (netBalance / totalIncome) * 100}.
     * <p>Returns 0.00 when total income is zero or negative to avoid division by zero.</p>
     *
     * @return savings rate percentage with 2 decimal places
     */
    public BigDecimal savingsRate() {
        if (totalIncome.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return netBalance.getAmount()
                .divide(totalIncome.getAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}

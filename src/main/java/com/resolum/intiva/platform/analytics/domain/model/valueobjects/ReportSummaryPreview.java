package com.resolum.intiva.platform.analytics.domain.model.valueobjects;

import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

import java.time.LocalDate;
import java.util.List;

/**
 * Immutable preview of a financial report for the requested owner and period.
 *
 * <p>This value object is returned by the report preview endpoint and contains
 * aggregated financial metrics (total income, total expenses, net balance), the
 * total transaction count, and a list of the top 5 expense categories with their
 * respective totals and percentages.</p>
 *
 * @param totalIncome      the sum of all income transactions in the period
 * @param totalExpenses    the sum of all expense transactions in the period
 * @param netBalance       the difference between total income and total expenses
 * @param transactionCount the total number of transactions that match the filter
 * @param topCategories    the top 5 expense categories ranked by total amount descending
 * @param periodStart      the inclusive start date of the analysis period
 * @param periodEnd        the inclusive end date of the analysis period
 * @param ownerType        the scope type of the owner (INDIVIDUAL or FAMILY)
 * @param ownerId          the unique identifier of the report owner
 */
public record ReportSummaryPreview(
        Money totalIncome,
        Money totalExpenses,
        Money netBalance,
        int transactionCount,
        List<CategoryExpenseSummary> topCategories,
        LocalDate periodStart,
        LocalDate periodEnd,
        OwnerTypes ownerType,
        String ownerId
) {
}

package com.resolum.intiva.platform.analytics.application.internal.queryhandlers;

import com.resolum.intiva.platform.analytics.application.internal.outboundservices.acl.AnalyticsExternalTransactionService;
import com.resolum.intiva.platform.analytics.domain.model.queries.GetReportPreviewQuery;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.CategoryExpenseSummary;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportSummaryPreview;
import com.resolum.intiva.platform.analytics.domain.services.ReportQueryService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Implementation of the {@link ReportQueryService} interface that computes report
 * previews by reading data from the finances bounded context through the ACL layer.
 *
 * <p>Unlike {@link com.resolum.intiva.platform.analytics.domain.services.AnalyticsQueryService
 * AnalyticsQueryService}, this service does not use caching — every preview request
 * is computed on the fly from source transactions to provide up-to-date results.</p>
 */
@Slf4j
@Service
public class ReportQueryServiceImpl implements ReportQueryService {

    /**
     * ACL service used to access transaction data from the finances bounded context.
     */
    private final AnalyticsExternalTransactionService externalService;

    /**
     * Creates the report query service with its required ACL dependency.
     *
     * @param externalService ACL service for accessing transaction data
     */
    public ReportQueryServiceImpl(AnalyticsExternalTransactionService externalService) {
        this.externalService = externalService;
    }

    /**
     * Computes a financial report preview for the given query parameters.
     *
     * <p>The preview is computed entirely on the fly:
     * <ol>
     *   <li>Transactions matching the owner and period are fetched via the ACL layer.</li>
     *   <li>If a {@code categoryId} is specified, the list is filtered to only include
     *       transactions belonging to that category.</li>
     *   <li>Income and expense totals are aggregated separately.</li>
     *   <li>Expenses are grouped by category and sorted by total amount descending;
     *       the top 5 categories are returned.</li>
     * </ol></p>
     *
     * @param query the query parameters (owner, period, optional category filter)
     * @return a fully populated {@link ReportSummaryPreview} with aggregated metrics
     */
    @Override
    public ReportSummaryPreview getReportPreview(GetReportPreviewQuery query) {
        log.info("Computing report preview for ownerId={}, ownerType={}, period=[{}, {}]",
                query.ownerId(), query.ownerType(), query.periodStart(), query.periodEnd());

        var ownerId = Long.parseLong(query.ownerId());
        var transactions = externalService.getTransactionsByOwnerAndPeriod(
                ownerId, query.ownerType(), query.periodStart(), query.periodEnd());

        if (query.categoryId() != null && !query.categoryId().isBlank()) {
            var catIdLong = Long.parseLong(query.categoryId());
            transactions = transactions.stream()
                    .filter(tx -> tx.getCategoryId() != null && catIdLong == tx.getCategoryId().getValue())
                    .toList();
        }

        var currency = resolveCurrency(transactions);
        var zero = new Money(BigDecimal.ZERO, currency);
        var totalIncome = zero;
        var totalExpenses = zero;
        var expensesByCategory = new LinkedHashMap<Long, List<Transaction>>();

        for (var tx : transactions) {
            if (tx.getTransactionType() == TransactionTypes.INCOME) {
                totalIncome = totalIncome.add(tx.getAmount());
            } else if (tx.getTransactionType() == TransactionTypes.EXPENSE) {
                totalExpenses = totalExpenses.add(tx.getAmount());
                var catId = tx.getCategoryId() != null ? tx.getCategoryId().getValue() : null;
                if (catId != null) {
                    expensesByCategory.computeIfAbsent(catId, k -> new ArrayList<>()).add(tx);
                }
            }
        }

        var netBalance = totalIncome.subtract(totalExpenses);
        var expenseTotal = totalExpenses;

        var topCategories = expensesByCategory.entrySet().stream()
                .map(entry -> {
                    var catId = entry.getKey();
                    var catTxns = entry.getValue();
                    var total = catTxns.stream()
                            .map(Transaction::getAmount)
                            .reduce(Money::add)
                            .orElse(zero);
                    var percentage = expenseTotal.getAmount().compareTo(BigDecimal.ZERO) > 0
                            ? total.getAmount()
                                    .divide(expenseTotal.getAmount(), 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100))
                                    .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                    var categoryInfo = externalService.getCategoryColorAndNameById(catId);
                    return new CategoryExpenseSummary(
                            String.valueOf(catId),
                            categoryInfo.getRight(),
                            categoryInfo.getLeft(),
                            total,
                            catTxns.size(),
                            percentage);
                })
                .sorted(Comparator.comparing(CategoryExpenseSummary::totalAmount,
                                (m1, m2) -> m1.getAmount().compareTo(m2.getAmount()))
                        .reversed())
                .limit(5)
                .toList();

        log.info("Report preview computed for ownerId={}: income={}, expenses={}, categories={}",
                query.ownerId(), totalIncome.getAmount(), totalExpenses.getAmount(), topCategories.size());

        return new ReportSummaryPreview(
                totalIncome, totalExpenses, netBalance,
                transactions.size(), topCategories,
                query.periodStart(), query.periodEnd(),
                query.ownerType(), query.ownerId());
    }

    /**
     * Resolves the currency code from the first available transaction, defaulting to
     * {@link CurrencyCodes#PEN} when the list is empty.
     *
     * @param transactions list of transactions to inspect
     * @return the currency code of the first transaction, or PEN if none exist
     */
    private CurrencyCodes resolveCurrency(List<Transaction> transactions) {
        return transactions.stream()
                .findFirst()
                .map(tx -> tx.getAmount().currencyCode())
                .orElse(CurrencyCodes.PEN);
    }
}

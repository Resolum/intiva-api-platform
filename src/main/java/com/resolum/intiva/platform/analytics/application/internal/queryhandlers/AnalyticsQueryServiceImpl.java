package com.resolum.intiva.platform.analytics.application.internal.queryhandlers;

import com.resolum.intiva.platform.analytics.application.internal.outboundservices.acl.AnalyticsExternalTransactionService;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsSummary;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SavingGoalAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SpendingLimitAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.queries.*;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.*;
import com.resolum.intiva.platform.analytics.domain.services.AnalyticsCachePort;
import com.resolum.intiva.platform.analytics.domain.services.AnalyticsQueryService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.savings.domain.model.aggregates.SavingGoal;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link AnalyticsQueryService} interface that computes analytics
 * by reading data from the finances and savings bounded contexts through the ACL layer.
 *
 * <p>This service applies a cache-aside pattern: on each query it first attempts to retrieve
 * the analytics from the {@link AnalyticsCachePort Redis cache}. On a cache hit the cached
 * value is returned immediately. On a cache miss the analytics are computed from source data,
 * stored in the cache, and then returned.</p>
 *
 * <p>Methods that return derived data ({@code categories/ranking}, {@code trend}) are not
 * cached — they are always computed on the fly from the underlying source data.</p>
 */
@Slf4j
@Service
public class AnalyticsQueryServiceImpl implements AnalyticsQueryService {

    /**
     * ACL service used to access data from the finances and savings bounded contexts.
     */
    private final AnalyticsExternalTransactionService externalService;

    /**
     * Cache port for caching computed analytics.
     */
    private final AnalyticsCachePort cachePort;

    /**
     * Creates the analytics query service with its required ACL and cache dependencies.
     *
     * @param externalService ACL service for accessing external bounded contexts
     * @param cachePort       cache port for storing/retrieving computed analytics
     */
    public AnalyticsQueryServiceImpl(AnalyticsExternalTransactionService externalService, AnalyticsCachePort cachePort) {
        this.externalService = externalService;
        this.cachePort = cachePort;
    }

    /**
     * Computes a financial summary for the given owner and period using a cache-aside pattern.
     *
     * <p>On a cache hit the cached summary is returned immediately. On a cache miss the
     * summary is computed from source transactions via the ACL layer, persisted in Redis,
     * and then returned.</p>
     *
     * <p>Income and expense transactions are aggregated separately. Expenses are further grouped by
     * category, and each category's percentage of the total is calculated.</p>
     *
     * @param query parameters identifying the owner, period, and scope
     * @return a fully populated {@link AnalyticsSummary}
     */
    @Override
    public AnalyticsSummary handle(GetAnalyticsSummaryByOwnerQuery query) {
        var cached = cachePort.findAnalyticsSummary(
                query.ownerId(), query.ownerType(), query.periodType(),
                query.periodStart(), query.periodEnd());
        if (cached != null) {
            log.info("Cache hit for analytics summary: ownerId={}", query.ownerId());
            return cached;
        }
        log.info("Cache miss for analytics summary: ownerId={}, periodType={}, periodStart={}, periodEnd={}",
                query.ownerId(), query.periodType(), query.periodStart(), query.periodEnd());
        var ownerId = Long.parseLong(query.ownerId());
        var transactions = externalService.getTransactionsByOwnerAndPeriod(
                ownerId, query.ownerType(), query.periodStart(), query.periodEnd());

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

        var categorySummaries = expensesByCategory.entrySet().stream()
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
                .toList();

        var result = new AnalyticsSummary(query.ownerType(), query.ownerId(), query.periodType(),
                query.periodStart(), query.periodEnd(), totalIncome, totalExpenses, netBalance,
                categorySummaries);
        cachePort.saveAnalyticsSummary(result);
        log.info("Analytics summary computed and cached for ownerId={}: income={}, expenses={}, categories={}",
                query.ownerId(), totalIncome.getAmount(), totalExpenses.getAmount(), categorySummaries.size());
        return result;
    }

    /**
     * Computes spending limit analytics for the given owner using a cache-aside pattern.
     *
     * <p>On a cache hit the cached analytics are returned immediately. On a cache miss the
     * analytics are computed from spending limits via the ACL layer, persisted in Redis,
     * and then returned.</p>
     *
     * <p>Limits are classified into SAFE, WARNING, or EXCEEDED based on their current
     * usage percentage.</p>
     *
     * @param query parameters identifying the owner and scope
     * @return a fully populated {@link SpendingLimitAnalytics}
     */
    @Override
    public SpendingLimitAnalytics handle(GetSpendingLimitAnalyticsByOwnerQuery query) {
        var cached = cachePort.findSpendingLimitAnalytics(
                query.ownerId(), query.ownerType(), query.periodType());
        if (cached != null) {
            log.info("Cache hit for spending limit analytics: ownerId={}", query.ownerId());
            return cached;
        }
        log.info("Cache miss for spending limit analytics: ownerId={}", query.ownerId());
        var ownerId = Long.parseLong(query.ownerId());
        var limits = externalService.getSpendingLimitsByOwner(ownerId, query.ownerType());

        var totalLimitsSet = limits.size();
        var limitsExceeded = 0;
        var limitsAtWarning = 0;
        var limitsSafe = 0;
        var details = new ArrayList<SpendingLimitDetail>();

        for (var limit : limits) {
            var usage = calculateUsagePercentage(limit);
            var status = resolveAnalyticsStatus(usage);
            var categoryName = externalService.getCategoryNameById(limit.getTargetId());

            details.add(new SpendingLimitDetail(
                    String.valueOf(limit.getId()),
                    String.valueOf(limit.getTargetId()),
                    categoryName,
                    limit.getLimitAmount(),
                    limit.getSpentAmount(),
                    usage,
                    status));

            switch (status) {
                case EXCEEDED -> limitsExceeded++;
                case WARNING -> limitsAtWarning++;
                case SAFE -> limitsSafe++;
            }
        }

        var spendingResult = new SpendingLimitAnalytics(query.ownerType(), query.ownerId(), query.periodType(),
                totalLimitsSet, limitsExceeded, limitsAtWarning, limitsSafe, details);
        cachePort.saveSpendingLimitAnalytics(spendingResult);
        log.info("Spending limit analytics computed and cached for ownerId={}: total={}, exceeded={}, warning={}, safe={}",
                query.ownerId(), totalLimitsSet, limitsExceeded, limitsAtWarning, limitsSafe);
        return spendingResult;
    }

    /**
     * Computes saving goal analytics for the given owner using a cache-aside pattern.
     *
     * <p>On a cache hit the cached analytics are returned immediately. On a cache miss the
     * analytics are computed from saving goals via the ACL layer, persisted in Redis,
     * and then returned.</p>
     *
     * <p>The analytics include completion rates, overall progress, and a per-goal breakdown.</p>
     *
     * @param query parameters identifying the owner and scope
     * @return a fully populated {@link SavingGoalAnalytics}
     */
    @Override
    public SavingGoalAnalytics handle(GetSavingGoalAnalyticsByOwnerQuery query) {
        var cached = cachePort.findSavingGoalAnalytics(query.ownerId(), query.ownerType());
        if (cached != null) {
            log.info("Cache hit for saving goal analytics: ownerId={}", query.ownerId());
            return cached;
        }
        log.info("Cache miss for saving goal analytics: ownerId={}", query.ownerId());
        var goals = externalService.getSavingGoalsByOwner(query.ownerId(), query.ownerType());

        var totalGoals = goals.size();
        var goalsCompleted = 0;
        var goalsInProgress = 0;
        var goalsUncompleted = 0;
        var details = new ArrayList<SavingGoalDetail>();

        Money totalTargetAmount = null;
        Money totalCurrentAmount = null;

        for (var goal : goals) {
            switch (goal.getStatus()) {
                case COMPLETED -> goalsCompleted++;
                case INPROGRESS -> goalsInProgress++;
                case UNCOMPLETED -> goalsUncompleted++;
            }

            if (totalTargetAmount == null) {
                totalTargetAmount = goal.getTargetAmount();
                totalCurrentAmount = goal.getCurrentAmount();
            } else {
                totalTargetAmount = totalTargetAmount.add(goal.getTargetAmount());
                totalCurrentAmount = totalCurrentAmount.add(goal.getCurrentAmount());
            }

            var progress = goal.getTargetAmount().getAmount().compareTo(BigDecimal.ZERO) > 0
                    ? goal.getCurrentAmount().getAmount()
                            .divide(goal.getTargetAmount().getAmount(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

            var deadlineDate = goal.getDeadline() != null
                    ? goal.getDeadline().atZone(ZoneId.systemDefault()).toLocalDate()
                    : null;
            var daysRemaining = deadlineDate != null
                    ? ChronoUnit.DAYS.between(LocalDate.now(), deadlineDate)
                    : 0L;

            details.add(new SavingGoalDetail(
                    String.valueOf(goal.getId()),
                    goal.getTitle(),
                    goal.getTargetAmount(),
                    goal.getCurrentAmount(),
                    progress,
                    deadlineDate,
                    goal.getStatus(),
                    Math.max(0, daysRemaining)));
        }

        var overallProgress = totalTargetAmount != null
                && totalTargetAmount.getAmount().compareTo(BigDecimal.ZERO) > 0
                ? totalCurrentAmount.getAmount()
                        .divide(totalTargetAmount.getAmount(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        var savingResult = new SavingGoalAnalytics(query.ownerType(), query.ownerId(),
                totalGoals, goalsCompleted, goalsInProgress, goalsUncompleted,
                totalTargetAmount, totalCurrentAmount, overallProgress, details);
        cachePort.saveSavingGoalAnalytics(savingResult);
        log.info("Saving goal analytics computed and cached for ownerId={}: total={}, completed={}, progress={}%",
                query.ownerId(), totalGoals, goalsCompleted, overallProgress);
        return savingResult;
    }

    /**
     * Computes a ranking of the top N expense categories for the given owner and period.
     * <p>Only EXPENSE transactions with an assigned category are considered. Results are sorted by
     * total amount descending and limited to {@code query.limit()} entries.</p>
     *
     * @param query parameters identifying the owner, period, and maximum number of categories
     * @return a ranked list of category expense summaries
     */
    @Override
    public List<CategoryExpenseSummary> handle(GetCategoryExpenseRankingQuery query) {
        log.info("Computing category expense ranking for ownerId={}, periodType={}, limit={}",
                query.ownerId(), query.periodType(), query.limit());
        var ownerId = Long.parseLong(query.ownerId());
        var transactions = externalService.getTransactionsByOwnerAndPeriod(
                ownerId, query.ownerType(), query.periodStart(), query.periodEnd());

        var expenseTransactions = transactions.stream()
                .filter(tx -> tx.getTransactionType() == TransactionTypes.EXPENSE)
                .filter(tx -> tx.getCategoryId() != null)
                .toList();

        var currency = resolveCurrency(expenseTransactions);
        var zero = new Money(BigDecimal.ZERO, currency);

        var totalExpenseAmount = expenseTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(Money::add)
                .orElse(zero);

        var groupedByCategory = expenseTransactions.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategoryId().getValue(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    var total = list.stream()
                                            .map(Transaction::getAmount)
                                            .reduce(Money::add)
                                            .orElse(zero);
                                    var percentage = totalExpenseAmount.getAmount().compareTo(BigDecimal.ZERO) > 0
                                            ? total.getAmount()
                                                    .divide(totalExpenseAmount.getAmount(), 4, RoundingMode.HALF_UP)
                                                    .multiply(BigDecimal.valueOf(100))
                                                    .setScale(2, RoundingMode.HALF_UP)
                                            : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                                    var categoryInfo = externalService.getCategoryColorAndNameById(list.get(0).getCategoryId().getValue());
                                    return new CategoryExpenseSummary(
                                            String.valueOf(list.get(0).getCategoryId().getValue()),
                                            categoryInfo.getRight(),
                                            categoryInfo.getLeft(),
                                            total,
                                            list.size(),
                                            percentage);
                                })));

        var result = groupedByCategory.values().stream()
                .sorted(Comparator.comparing(CategoryExpenseSummary::totalAmount,
                                (m1, m2) -> m1.getAmount().compareTo(m2.getAmount()))
                        .reversed())
                .limit(query.limit())
                .toList();

        log.info("Category expense ranking computed for ownerId={}: {} categories returned",
                query.ownerId(), result.size());
        return result;
    }

    /**
     * Computes the income vs expense trend for the last N periods relative to the current date.
     * <p>Each period (day, week, month, or year) is evaluated independently. Income and expenses are
     * summed per period and returned as a chronological list.</p>
     *
     * @param query parameters identifying the owner, period type, and number of periods
     * @return a chronological list of period trends
     */
    @Override
    public List<AnalyticsPeriodTrend> handle(GetIncomeVsExpenseTrendQuery query) {
        log.info("Computing income vs expense trend for ownerId={}, periodType={}, lastNPeriods={}",
                query.ownerId(), query.periodType(), query.lastNPeriods());
        var ownerId = Long.parseLong(query.ownerId());
        var trends = new ArrayList<AnalyticsPeriodTrend>();

        for (int i = query.lastNPeriods() - 1; i >= 0; i--) {
            AnalyticsPeriod period;
            if (i == 0) {
                period = AnalyticsPeriod.current(query.periodType());
            } else {
                var current = AnalyticsPeriod.current(query.periodType());
                period = switch (query.periodType()) {
                    case DAILY -> new AnalyticsPeriod(PeriodTypes.DAILY,
                            current.start().minusDays(i), current.end().minusDays(i));
                    case WEEKLY -> new AnalyticsPeriod(PeriodTypes.WEEKLY,
                            current.start().minusWeeks(i), current.end().minusWeeks(i));
                    case MONTHLY -> new AnalyticsPeriod(PeriodTypes.MONTHLY,
                            current.start().minusMonths(i), current.end().minusMonths(i));
                    case ANNUAL -> new AnalyticsPeriod(PeriodTypes.ANNUAL,
                            current.start().minusYears(i), current.end().minusYears(i));
                };
            }

            var transactionsForPeriod = externalService.getTransactionsByOwnerAndPeriod(
                    ownerId, query.ownerType(), period.start(), period.end());

            var currency = resolveCurrency(transactionsForPeriod);
            var zero = new Money(BigDecimal.ZERO, currency);
            var totalIncome = zero;
            var totalExpenses = zero;

            for (var tx : transactionsForPeriod) {
                if (tx.getTransactionType() == TransactionTypes.INCOME) {
                    totalIncome = totalIncome.add(tx.getAmount());
                } else if (tx.getTransactionType() == TransactionTypes.EXPENSE) {
                    totalExpenses = totalExpenses.add(tx.getAmount());
                }
            }

            var netBalance = totalIncome.subtract(totalExpenses);
            trends.add(new AnalyticsPeriodTrend(period, totalIncome, totalExpenses, netBalance));
        }

        log.info("Income vs expense trend computed for ownerId={}: {} periods",
                query.ownerId(), trends.size());
        return trends;
    }

    /**
     * Resolves the currency code from the first available transaction, defaulting to PEN when the list
     * is empty.
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

    /**
     * Calculates the usage percentage of a spending limit as {@code (spentAmount / limitAmount) * 100}.
     *
     * @param limit the spending limit to evaluate
     * @return usage percentage with 2 decimal places
     */
    private BigDecimal calculateUsagePercentage(SpendingLimit limit) {
        var limitAmount = limit.getLimitAmount().getAmount();
        if (limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return limit.getSpentAmount().getAmount()
                .divide(limitAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Resolves the analytics status from a usage percentage.
     * <ul>
     *     <li>{@link SpendingLimitAnalyticsStatus#SAFE} — less than 80% used</li>
     *     <li>{@link SpendingLimitAnalyticsStatus#WARNING} — 80% or more used</li>
     *     <li>{@link SpendingLimitAnalyticsStatus#EXCEEDED} — 100% or more used</li>
     * </ul>
     *
     * @param usagePercentage the current usage percentage
     * @return the corresponding analytics status
     */
    private SpendingLimitAnalyticsStatus resolveAnalyticsStatus(BigDecimal usagePercentage) {
        if (usagePercentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return SpendingLimitAnalyticsStatus.EXCEEDED;
        }
        if (usagePercentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return SpendingLimitAnalyticsStatus.WARNING;
        }
        return SpendingLimitAnalyticsStatus.SAFE;
    }
}

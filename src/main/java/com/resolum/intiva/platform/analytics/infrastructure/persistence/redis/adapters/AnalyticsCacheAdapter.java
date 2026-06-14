package com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.adapters;

import com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsSummary;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SavingGoalAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.aggregates.SpendingLimitAnalytics;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.CategoryExpenseSummary;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.SavingGoalDetail;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.SpendingLimitDetail;
import com.resolum.intiva.platform.analytics.domain.services.AnalyticsCachePort;
import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.converters.BytesToMoneyConverter;
import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.converters.MoneyToBytesConverter;
import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.entities.AnalyticsSummaryCacheEntity;
import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.entities.SavingGoalAnalyticsCacheEntity;
import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.entities.SpendingLimitAnalyticsCacheEntity;
import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.repositories.AnalyticsSummaryCacheRepository;
import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.repositories.SavingGoalAnalyticsCacheRepository;
import com.resolum.intiva.platform.analytics.infrastructure.persistence.redis.repositories.SpendingLimitAnalyticsCacheRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Redis-backed implementation of the {@link AnalyticsCachePort} port.
 *
 * <p>This adapter persists computed analytics aggregates in Redis using a structured key format.
 * Each analytics type uses its own Redis hash namespace and repository:</p>
 * <ul>
 *     <li>{@code analytics:summary} — financial summaries keyed by {@code ownerType:ownerId:periodType:start:end}</li>
 *     <li>{@code analytics:spending} — spending limit analytics keyed by {@code ownerType:ownerId:periodType}</li>
 *     <li>{@code analytics:saving} — saving goal analytics keyed by {@code ownerType:ownerId}</li>
 * </ul>
 *
 * <p>Money values are serialized to byte arrays via {@link MoneyToBytesConverter} and deserialized
 * via {@link BytesToMoneyConverter}. Complex list fields are stored as JSON strings.</p>
 */
@Slf4j
@Component
public class AnalyticsCacheAdapter implements AnalyticsCachePort {

    /**
     * Redis repository for cached analytics summary entities.
     */
    private final AnalyticsSummaryCacheRepository summaryRepository;

    /**
     * Redis repository for cached spending limit analytics entities.
     */
    private final SpendingLimitAnalyticsCacheRepository spendingRepository;

    /**
     * Redis repository for cached saving goal analytics entities.
     */
    private final SavingGoalAnalyticsCacheRepository savingRepository;

    /**
     * Jackson ObjectMapper configured for Redis serialisation.
     */
    private final ObjectMapper objectMapper;

    /**
     * Converter that serialises Money value objects to byte arrays for Redis storage.
     */
    private final MoneyToBytesConverter moneyToBytesConverter;

    /**
     * Converter that deserialises byte arrays from Redis back into Money value objects.
     */
    private final BytesToMoneyConverter bytesToMoneyConverter;

    /**
     * Creates the Redis cache adapter with its repository and converter dependencies.
     *
     * @param summaryRepository     repository for analytics summary cache entities
     * @param spendingRepository    repository for spending limit analytics cache entities
     * @param savingRepository      repository for saving goal analytics cache entities
     * @param objectMapper          redis-specific ObjectMapper for JSON serialisation
     * @param moneyToBytesConverter converter from Money to byte array
     * @param bytesToMoneyConverter converter from byte array to Money
     */
    public AnalyticsCacheAdapter(
            AnalyticsSummaryCacheRepository summaryRepository,
            SpendingLimitAnalyticsCacheRepository spendingRepository,
            SavingGoalAnalyticsCacheRepository savingRepository,
            @Qualifier("redisObjectMapper") ObjectMapper objectMapper,
            MoneyToBytesConverter moneyToBytesConverter,
            BytesToMoneyConverter bytesToMoneyConverter
    ) {
        this.summaryRepository = summaryRepository;
        this.spendingRepository = spendingRepository;
        this.savingRepository = savingRepository;
        this.objectMapper = objectMapper;
        this.moneyToBytesConverter = moneyToBytesConverter;
        this.bytesToMoneyConverter = bytesToMoneyConverter;
    }

    /**
     * Caches an analytics summary in Redis.
     *
     * <p>The Redis key is constructed from the owner type, owner id, period type, and period dates.
     * Money fields are serialised to bytes; the expense-by-category list is serialised to a JSON string.</p>
     *
     * @param summary the analytics summary to cache
     */
    @Override
    public void saveAnalyticsSummary(AnalyticsSummary summary) {
        var id = buildSummaryKey(
                summary.getOwnerId(), summary.getOwnerType(), summary.getPeriodType(),
                summary.getPeriodStart(), summary.getPeriodEnd());

        var entity = new AnalyticsSummaryCacheEntity();
        entity.setId(id);
        entity.setOwnerId(summary.getOwnerId());
        entity.setOwnerType(summary.getOwnerType().name());
        entity.setPeriodType(summary.getPeriodType().name());
        entity.setPeriodStart(summary.getPeriodStart().toString());
        entity.setPeriodEnd(summary.getPeriodEnd().toString());
        entity.setTotalIncome(moneyToBytesConverter.convert(summary.getTotalIncome()));
        entity.setTotalExpenses(moneyToBytesConverter.convert(summary.getTotalExpenses()));
        entity.setNetBalance(moneyToBytesConverter.convert(summary.getNetBalance()));
        entity.setSavingsRate(summary.savingsRate().toString());
        entity.setExpensesByCategory(serializeList(summary.getExpensesByCategory()));
        entity.setGeneratedAt(summary.getGeneratedAt().toString());

        summaryRepository.save(entity);
        log.debug("AnalyticsSummary cached with key={}", id);
    }

    /**
     * Retrieves a cached analytics summary from Redis.
     *
     * <p>If the key does not exist in the cache, the method returns null so the caller can
     * fall back to computing the summary from source data.</p>
     *
     * @param ownerId    owner identifier
     * @param ownerType  owner scope
     * @param periodType period granularity
     * @param periodStart period start date
     * @param periodEnd   period end date
     * @return the cached AnalyticsSummary, or null if not found
     */
    @Override
    public AnalyticsSummary findAnalyticsSummary(
            String ownerId, OwnerTypes ownerType, PeriodTypes periodType,
            LocalDate periodStart, LocalDate periodEnd) {
        var id = buildSummaryKey(ownerId, ownerType, periodType, periodStart, periodEnd);
        var opt = summaryRepository.findById(id);
        if (opt.isEmpty()) {
            return null;
        }
        var entity = opt.get();
        return new AnalyticsSummary(
                entity.getId(),
                OwnerTypes.valueOf(entity.getOwnerType()),
                entity.getOwnerId(),
                PeriodTypes.valueOf(entity.getPeriodType()),
                LocalDate.parse(entity.getPeriodStart()),
                LocalDate.parse(entity.getPeriodEnd()),
                bytesToMoneyConverter.convert(entity.getTotalIncome()),
                bytesToMoneyConverter.convert(entity.getTotalExpenses()),
                bytesToMoneyConverter.convert(entity.getNetBalance()),
                deserializeList(entity.getExpensesByCategory(), CategoryExpenseSummary.class),
                Instant.parse(entity.getGeneratedAt())
        );
    }

    /**
     * Caches spending limit analytics in Redis.
     *
     * <p>The Redis key is constructed from the owner type, owner id, and period type.
     * The spending limit detail list is serialised to a JSON string.</p>
     *
     * @param analytics the spending limit analytics to cache
     */
    @Override
    public void saveSpendingLimitAnalytics(SpendingLimitAnalytics analytics) {
        var id = buildSpendingKey(
                analytics.getOwnerId(), analytics.getOwnerType(), analytics.getPeriodType());

        var entity = new SpendingLimitAnalyticsCacheEntity();
        entity.setId(id);
        entity.setOwnerId(analytics.getOwnerId());
        entity.setOwnerType(analytics.getOwnerType().name());
        entity.setPeriodType(analytics.getPeriodType().name());
        entity.setTotalLimitsSet(analytics.getTotalLimitsSet());
        entity.setLimitsExceeded(analytics.getLimitsExceeded());
        entity.setLimitsAtWarning(analytics.getLimitsAtWarning());
        entity.setLimitsSafe(analytics.getLimitsSafe());
        entity.setExceededRate(analytics.exceededRate().toString());
        entity.setWarningRate(analytics.warningRate().toString());
        entity.setDetails(serializeList(analytics.getDetails()));
        entity.setGeneratedAt(analytics.getGeneratedAt().toString());

        spendingRepository.save(entity);
        log.debug("SpendingLimitAnalytics cached with key={}", id);
    }

    /**
     * Retrieves cached spending limit analytics from Redis.
     *
     * <p>If the key does not exist in the cache, the method returns null so the caller can
     * fall back to computing the analytics from source data.</p>
     *
     * @param ownerId    owner identifier
     * @param ownerType  owner scope
     * @param periodType period granularity
     * @return the cached SpendingLimitAnalytics, or null if not found
     */
    @Override
    public SpendingLimitAnalytics findSpendingLimitAnalytics(
            String ownerId, OwnerTypes ownerType, PeriodTypes periodType) {
        var id = buildSpendingKey(ownerId, ownerType, periodType);
        var opt = spendingRepository.findById(id);
        if (opt.isEmpty()) {
            return null;
        }
        var entity = opt.get();
        return new SpendingLimitAnalytics(
                entity.getId(),
                OwnerTypes.valueOf(entity.getOwnerType()),
                entity.getOwnerId(),
                PeriodTypes.valueOf(entity.getPeriodType()),
                entity.getTotalLimitsSet(),
                entity.getLimitsExceeded(),
                entity.getLimitsAtWarning(),
                entity.getLimitsSafe(),
                deserializeList(entity.getDetails(), SpendingLimitDetail.class),
                Instant.parse(entity.getGeneratedAt())
        );
    }

    /**
     * Caches saving goal analytics in Redis.
     *
     * <p>The Redis key is constructed from the owner type and owner id.
     * Money fields are serialised to bytes; the saving goal detail list is serialised
     * to a JSON string.</p>
     *
     * @param analytics the saving goal analytics to cache
     */
    @Override
    public void saveSavingGoalAnalytics(SavingGoalAnalytics analytics) {
        var id = buildSavingKey(analytics.getOwnerId(), analytics.getOwnerType());

        var entity = new SavingGoalAnalyticsCacheEntity();
        entity.setId(id);
        entity.setOwnerId(analytics.getOwnerId());
        entity.setOwnerType(analytics.getOwnerType().name());
        entity.setTotalGoals(analytics.getTotalGoals());
        entity.setGoalsCompleted(analytics.getGoalsCompleted());
        entity.setGoalsInProgress(analytics.getGoalsInProgress());
        entity.setGoalsUncompleted(analytics.getGoalsUncompleted());

        if (analytics.getTotalTargetAmount() != null) {
            entity.setTotalTargetAmount(moneyToBytesConverter.convert(analytics.getTotalTargetAmount()));
        }
        if (analytics.getTotalCurrentAmount() != null) {
            entity.setTotalCurrentAmount(moneyToBytesConverter.convert(analytics.getTotalCurrentAmount()));
        }

        entity.setOverallProgress(analytics.getOverallProgress().toString());
        entity.setCompletionRate(analytics.completionRate().toString());
        entity.setDetails(serializeList(analytics.getDetails()));
        entity.setGeneratedAt(analytics.getGeneratedAt().toString());

        savingRepository.save(entity);
        log.debug("SavingGoalAnalytics cached with key={}", id);
    }

    /**
     * Retrieves cached saving goal analytics from Redis.
     *
     * <p>If the key does not exist in the cache, the method returns null so the caller can
     * fall back to computing the analytics from source data.</p>
     *
     * @param ownerId   owner identifier
     * @param ownerType owner scope
     * @return the cached SavingGoalAnalytics, or null if not found
     */
    @Override
    public SavingGoalAnalytics findSavingGoalAnalytics(String ownerId, OwnerTypes ownerType) {
        var id = buildSavingKey(ownerId, ownerType);
        var opt = savingRepository.findById(id);
        if (opt.isEmpty()) {
            return null;
        }
        var entity = opt.get();
        Money totalTargetAmount = null;
        Money totalCurrentAmount = null;
        if (entity.getTotalTargetAmount() != null) {
            totalTargetAmount = bytesToMoneyConverter.convert(entity.getTotalTargetAmount());
        }
        if (entity.getTotalCurrentAmount() != null) {
            totalCurrentAmount = bytesToMoneyConverter.convert(entity.getTotalCurrentAmount());
        }

        return new SavingGoalAnalytics(
                entity.getId(),
                OwnerTypes.valueOf(entity.getOwnerType()),
                entity.getOwnerId(),
                entity.getTotalGoals(),
                entity.getGoalsCompleted(),
                entity.getGoalsInProgress(),
                entity.getGoalsUncompleted(),
                totalTargetAmount,
                totalCurrentAmount,
                new BigDecimal(entity.getOverallProgress()),
                deserializeList(entity.getDetails(), SavingGoalDetail.class),
                Instant.parse(entity.getGeneratedAt())
        );
    }

    /**
     * Evicts all cached analytics data for the given owner from Redis.
     *
     * <p>This method deletes cache entries from all three Redis namespaces
     * (summary, spending, saving) for the specified owner scope.</p>
     *
     * @param ownerId   owner identifier
     * @param ownerType owner scope
     */
    @Override
    public void evictByOwner(String ownerId, OwnerTypes ownerType) {
        summaryRepository.deleteAllByOwnerIdAndOwnerType(ownerId, ownerType.name());
        spendingRepository.deleteAllByOwnerIdAndOwnerType(ownerId, ownerType.name());
        savingRepository.deleteAllByOwnerIdAndOwnerType(ownerId, ownerType.name());
        log.info("Evicted all analytics cache for ownerId={}, ownerType={}", ownerId, ownerType);
    }

    /**
     * Builds the Redis key for an analytics summary entity.
     * <p>Key format: {@code {ownerType}:{ownerId}:{periodType}:{periodStart}:{periodEnd}}</p>
     *
     * @param ownerId    owner identifier
     * @param ownerType  owner scope
     * @param periodType period granularity
     * @param periodStart period start date
     * @param periodEnd   period end date
     * @return the structured Redis key
     */
    private String buildSummaryKey(
            String ownerId, OwnerTypes ownerType, PeriodTypes periodType,
            LocalDate periodStart, LocalDate periodEnd) {
        return ownerType.name() + ":" + ownerId + ":" + periodType.name()
                + ":" + periodStart + ":" + periodEnd;
    }

    /**
     * Builds the Redis key for a spending limit analytics entity.
     * <p>Key format: {@code {ownerType}:{ownerId}:{periodType}}</p>
     *
     * @param ownerId    owner identifier
     * @param ownerType  owner scope
     * @param periodType period granularity
     * @return the structured Redis key
     */
    private String buildSpendingKey(String ownerId, OwnerTypes ownerType, PeriodTypes periodType) {
        return ownerType.name() + ":" + ownerId + ":" + periodType.name();
    }

    /**
     * Builds the Redis key for a saving goal analytics entity.
     * <p>Key format: {@code {ownerType}:{ownerId}}</p>
     *
     * @param ownerId   owner identifier
     * @param ownerType owner scope
     * @return the structured Redis key
     */
    private String buildSavingKey(String ownerId, OwnerTypes ownerType) {
        return ownerType.name() + ":" + ownerId;
    }

    /**
     * Serialises a list of objects to a JSON string for Redis storage.
     *
     * @param list the list to serialise
     * @return JSON string representation, or {@code "[]"} on failure
     */
    private String serializeList(Object list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.error("Failed to serialize list to JSON string", e);
            return "[]";
        }
    }

    /**
     * Deserialises a JSON string from Redis into a list of objects of the given type.
     *
     * @param json         the JSON string to deserialise
     * @param elementClass the target element class
     * @param <T>          the element type
     * @return a list of deserialised elements, or an empty list on failure
     */
    private <T> List<T> deserializeList(String json, Class<T> elementClass) {
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (Exception e) {
            log.error("Failed to deserialize JSON string to list", e);
            return List.of();
        }
    }
}

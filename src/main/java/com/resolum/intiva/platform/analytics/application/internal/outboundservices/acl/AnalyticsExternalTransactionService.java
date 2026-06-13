package com.resolum.intiva.platform.analytics.application.internal.outboundservices.acl;

import com.resolum.intiva.platform.categories.interfaces.acl.CategoriesContextFacade;
import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.SpendingLimitRepository;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.TransactionRepository;
import com.resolum.intiva.platform.savings.domain.model.aggregates.SavingGoal;
import com.resolum.intiva.platform.savings.infrastructure.persistence.jpa.repositories.SavingGoalRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Anti-corruption layer (ACL) service that provides read access to data from the finances and savings
 * bounded contexts for analytics computation purposes.
 *
 * <p>This is the only entry point through which the analytics bounded context reads data from other
 * contexts, ensuring loose coupling and maintaining bounded context boundaries.</p>
 */
@Slf4j
@Service
public class AnalyticsExternalTransactionService {

    /**
     * Repository for accessing financial transactions from the finances bounded context.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Repository for accessing spending limits from the finances bounded context.
     */
    private final SpendingLimitRepository spendingLimitRepository;

    /**
     * Repository for accessing saving goals from the savings bounded context.
     */
    private final SavingGoalRepository savingGoalRepository;

    /**
     * Facade for accessing category metadata from the categories bounded context.
     */
    private final CategoriesContextFacade categoriesContextFacade;

    /**
     * Creates the analytics ACL service with the required repository and facade dependencies.
     *
     * @param transactionRepository    finances transaction repository
     * @param spendingLimitRepository  finances spending limit repository
     * @param savingGoalRepository     savings saving goal repository
     * @param categoriesContextFacade  categories context facade
     */
    public AnalyticsExternalTransactionService(
            TransactionRepository transactionRepository,
            SpendingLimitRepository spendingLimitRepository,
            SavingGoalRepository savingGoalRepository,
            CategoriesContextFacade categoriesContextFacade) {
        this.transactionRepository = transactionRepository;
        this.spendingLimitRepository = spendingLimitRepository;
        this.savingGoalRepository = savingGoalRepository;
        this.categoriesContextFacade = categoriesContextFacade;
    }

    /**
     * Retrieves all transactions for an owner within a date range, filtering by owner type.
     *
     * @param ownerId   the owner identifier
     * @param ownerType the owner scope (INDIVIDUAL or FAMILY)
     * @param start     inclusive start date of the period
     * @param end       inclusive end date of the period
     * @return list of matching transactions
     */
    public List<Transaction> getTransactionsByOwnerAndPeriod(Long ownerId, OwnerTypes ownerType,
                                                             LocalDate start, LocalDate end) {
        log.info("Fetching transactions for ownerId={}, ownerType={}, period=[{}, {}]",
                ownerId, ownerType, start, end);
        var allTransactions = transactionRepository.findTransactionByOwnerId(ownerId);
        var filtered = allTransactions.stream()
                .filter(tx -> tx.getOwnerTypes() == ownerType)
                .filter(tx -> {
                    var txDate = tx.getCreatedAt() != null
                            ? tx.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            : null;
                    return txDate != null && !txDate.isBefore(start) && !txDate.isAfter(end);
                })
                .toList();
        log.info("Found {} transactions for ownerId={} in the given period", filtered.size(), ownerId);
        return filtered;
    }

    /**
     * Retrieves all spending limits for an owner.
     *
     * @param ownerId   the owner identifier
     * @param ownerType the owner scope (INDIVIDUAL or FAMILY)
     * @return list of spending limits
     */
    public List<SpendingLimit> getSpendingLimitsByOwner(Long ownerId, OwnerTypes ownerType) {
        log.info("Fetching spending limits for ownerId={}, ownerType={}", ownerId, ownerType);
        var limits = spendingLimitRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
        log.info("Found {} spending limits for ownerId={}", limits.size(), ownerId);
        return limits;
    }

    /**
     * Retrieves all saving goals for an owner.
     * <p>For FAMILY owners the lookup is by ownerId; for INDIVIDUAL owners the lookup is by
     * numeric user id parsed from the string ownerId.</p>
     *
     * @param ownerId   the owner identifier (string, may represent a user id or group id)
     * @param ownerType the owner scope (INDIVIDUAL or FAMILY)
     * @return list of saving goals
     */
    public List<SavingGoal> getSavingGoalsByOwner(String ownerId, OwnerTypes ownerType) {
        log.info("Fetching saving goals for ownerId={}, ownerType={}", ownerId, ownerType);
        List<SavingGoal> goals;
        if (ownerType == OwnerTypes.FAMILY) {
            goals = savingGoalRepository.findAllByOwnerId(ownerId);
        } else {
            try {
                var userId = Long.parseLong(ownerId);
                goals = savingGoalRepository.findAllByActorUserId(userId);
            } catch (NumberFormatException e) {
                log.warn("Invalid numeric ownerId for INDIVIDUAL owner: {}", ownerId);
                return List.of();
            }
        }
        log.info("Found {} saving goals for ownerId={}", goals.size(), ownerId);
        return goals;
    }

    /**
     * Retrieves the color and name of a category by its identifier.
     *
     * @param categoryId the category identifier
     * @return an immutable pair containing (color, name)
     */
    public ImmutablePair<String, String> getCategoryColorAndNameById(Long categoryId) {
        return categoriesContextFacade.getCategoryColorAndIconById(categoryId);
    }

    /**
     * Retrieves the display name of a category by its identifier.
     *
     * @param categoryId the category identifier
     * @return the category display name
     */
    public String getCategoryNameById(Long categoryId) {
        return categoriesContextFacade.getCategoryNameById(categoryId);
    }
}

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

@Slf4j
@Service
public class AnalyticsExternalTransactionService {

    private final TransactionRepository transactionRepository;
    private final SpendingLimitRepository spendingLimitRepository;
    private final SavingGoalRepository savingGoalRepository;
    private final CategoriesContextFacade categoriesContextFacade;

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

    public List<SpendingLimit> getSpendingLimitsByOwner(Long ownerId, OwnerTypes ownerType) {
        log.info("Fetching spending limits for ownerId={}, ownerType={}", ownerId, ownerType);
        var limits = spendingLimitRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
        log.info("Found {} spending limits for ownerId={}", limits.size(), ownerId);
        return limits;
    }

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

    public ImmutablePair<String, String> getCategoryColorAndNameById(Long categoryId) {
        return categoriesContextFacade.getCategoryColorAndIconById(categoryId);
    }

    public String getCategoryNameById(Long categoryId) {
        return categoriesContextFacade.getCategoryNameById(categoryId);
    }
}

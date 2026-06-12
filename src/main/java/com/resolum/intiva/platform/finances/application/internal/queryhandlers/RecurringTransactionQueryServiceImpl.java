package com.resolum.intiva.platform.finances.application.internal.queryhandlers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.finances.domain.model.queries.GetRecurringTransactionByIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetRecurringTransactionsByOwnerIdAndOwnerTypeQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetRecurringTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.services.RecurringTransactionQueryService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Default query implementation for recurring transaction definitions.
 */
@Service
@Slf4j
public class RecurringTransactionQueryServiceImpl implements RecurringTransactionQueryService {

    /**
     * Repository used to retrieve recurring transaction definitions.
     */
    private final RecurringTransactionRepository recurringTransactionRepository;

    /**
     * Creates the query service with the repository dependency it needs.
     *
     * @param recurringTransactionRepository recurring transaction repository
     */
    public RecurringTransactionQueryServiceImpl(RecurringTransactionRepository recurringTransactionRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    /**
     * Retrieves one recurring transaction definition by id.
     *
     * @param query identifier query
     * @return matching aggregate if present
     */
    @Override
    public Optional<RecurringTransaction> handle(GetRecurringTransactionByIdQuery query) {
        log.info("Querying recurring transaction by ID. recurringTransactionId={}", query.recurringTransactionId());
        var result = recurringTransactionRepository.findById(query.recurringTransactionId());
        if (result.isPresent()) {
            log.info("Recurring transaction found. recurringTransactionId={}", query.recurringTransactionId());
        } else {
            log.warn("Recurring transaction not found. recurringTransactionId={}", query.recurringTransactionId());
        }
        return result;
    }

    /**
     * Retrieves recurring transaction definitions by owner id.
     *
     * @param query owner query
     * @return recurring transaction definitions owned by the given owner id
     */
    @Override
    public List<RecurringTransaction> handle(GetRecurringTransactionsByOwnerIdQuery query) {
        log.info("Querying recurring transactions by ownerId={}", query.ownerId());
        var result = recurringTransactionRepository.findByOwnerId(query.ownerId());
        log.info("Found {} recurring transactions for ownerId={}", result.size(), query.ownerId());
        return result;
    }

    /**
     * Retrieves recurring transaction definitions by owner id and owner type.
     *
     * @param query scoped owner query
     * @return recurring transaction definitions matching the given owner scope
     */
    @Override
    public List<RecurringTransaction> handle(GetRecurringTransactionsByOwnerIdAndOwnerTypeQuery query) {
        log.info("Querying recurring transactions by ownerId={} and ownerType={}", query.ownerId(), query.ownerType());
        var result = recurringTransactionRepository.findByOwnerIdAndOwnerType(query.ownerId(), query.ownerType());
        log.info("Found {} recurring transactions for ownerId={} and ownerType={}",
                result.size(), query.ownerId(), query.ownerType());
        return result;
    }
}

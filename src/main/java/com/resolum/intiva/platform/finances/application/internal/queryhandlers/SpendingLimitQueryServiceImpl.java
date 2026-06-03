package com.resolum.intiva.platform.finances.application.internal.queryhandlers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.queries.*;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.finances.domain.services.SpendingLimitQueryService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.SpendingLimitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link com.resolum.intiva.platform.finances.domain.services.SpendingLimitQueryService}.
 */
@Service
public class SpendingLimitQueryServiceImpl implements SpendingLimitQueryService {

    /**
     * Repository used to read spending limit data.
     */
    private final SpendingLimitRepository spendingLimitRepository;

    /**
     * Creates the query service with its repository dependency.
     *
     * @param spendingLimitRepository repository dependency
     */
    public SpendingLimitQueryServiceImpl(SpendingLimitRepository spendingLimitRepository) {
        this.spendingLimitRepository = spendingLimitRepository;
    }

    /**
     * Retrieves a spending limit by id.
     */
    @Override
    public Optional<SpendingLimit> handle(GetSpendingLimitByIdQuery query) {
        return spendingLimitRepository.findById(query.spendingLimitId());
    }

    /**
     * Retrieves limits for one owner regardless of owner scope.
     */
    @Override
    public List<SpendingLimit> handle(GetSpendingLimitsByOwnerIdQuery query) {
        return spendingLimitRepository.findByOwnerId(query.ownerId());
    }

    /**
     * Retrieves limits for one owner with an explicit owner scope.
     */
    @Override
    public List<SpendingLimit> handle(GetSpendingLimitsByOwnerIdAndOwnerTypeQuery query) {
        return spendingLimitRepository.findByOwnerIdAndOwnerType(query.ownerId(), query.ownerType());
    }

    /**
     * Retrieves limits that apply to a category target.
     */
    @Override
    public List<SpendingLimit> handle(GetSpendingLimitsByCategoryIdQuery query) {
        return spendingLimitRepository.findByTargetTypeAndTargetId(SpendingLimitTargetType.CATEGORY, query.categoryId());
    }

    /**
     * Retrieves limits that apply to a financial account target.
     */
    @Override
    public List<SpendingLimit> handle(GetSpendingLimitsByFinancialAccountIdQuery query) {
        return spendingLimitRepository.findByTargetTypeAndTargetId(SpendingLimitTargetType.FINANCIAL_ACCOUNT, query.financialAccountId());
    }
}

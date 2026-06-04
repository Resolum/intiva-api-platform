package com.resolum.intiva.platform.categories.application.internal.queryhandlers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.queries.GetAllFinancialAccountsByOwnerId;
import com.resolum.intiva.platform.categories.domain.model.queries.GetFinancialAccountByIdQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetFinancialAccountByOwnerId;
import com.resolum.intiva.platform.categories.domain.services.FinancialAccountQueryService;
import com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.FinancialAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the FinancialAccountQueryService interface.
 */
@Slf4j
@Service
public class FinancialAccountQueryServiceImpl implements FinancialAccountQueryService {

    // Repository for accessing financial account data from the database
    private final FinancialAccountRepository financialAccountRepository;

    // Constructor for dependency injection of the FinancialAccountRepository
    public FinancialAccountQueryServiceImpl(FinancialAccountRepository financialAccountRepository) {
        this.financialAccountRepository = financialAccountRepository;
    }

    /**
     * Retrieves all financial accounts associated with a specific owner ID.
     * @param query the query containing the owner ID
     * @return a list of FinancialAccount entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialAccount> handle(GetAllFinancialAccountsByOwnerId query) {
        log.info(
                "{} - Fetching all financial accounts for owner ID: {}",
                this.getClass().getSimpleName(),
                query.ownerId()
        );
        return financialAccountRepository.findAllByOwnerId(query.ownerId());
    }

    /**
     * Check if a financial account exists by id
     * @param query the query containing the financial account id
     * @return true if the financial account exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean handle(GetFinancialAccountByOwnerId query) {
        log.info(
                "{} - Checking if financial account exists for owner ID: {}",
                this.getClass().getSimpleName(),
                query.ownerId()
        );
        return financialAccountRepository.existsById(query.ownerId());
    }

    /**
     * Retrieves a financial account by its ID.
     * @param query the query containing the financial account ID
     * @return an Optional containing the FinancialAccount if found, or empty if not found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<FinancialAccount> handle(GetFinancialAccountByIdQuery query) {
        log.info(
                "{} - Fetching financial account by ID: {}",
                this.getClass().getSimpleName(),
                query.financialAccountId()
        );
        return financialAccountRepository.findById(query.financialAccountId());
    }
}

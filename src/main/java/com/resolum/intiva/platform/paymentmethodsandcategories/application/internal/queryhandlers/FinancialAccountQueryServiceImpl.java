package com.resolum.intiva.platform.paymentmethodsandcategories.application.internal.queryhandlers;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetAllFinancialAccountsByOwnerId;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetFinancialAccountByIdQuery;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetFinancialAccountByOwnerId;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.services.FinancialAccountQueryService;
import com.resolum.intiva.platform.paymentmethodsandcategories.infraestructure.persistence.jpa.repositories.FinancialAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the FinancialAccountQueryService interface.
 */
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
    public List<FinancialAccount> handle(GetAllFinancialAccountsByOwnerId query) {
        return financialAccountRepository.findAllByOwnerId(query.ownerId());
    }

    /**
     * Check if a financial account exists by id
     * @param query the query containing the financial account id
     * @return true if the financial account exists, false otherwise
     */
    @Override
    public boolean existsFinancialAccountById(GetFinancialAccountByOwnerId query) {
        return financialAccountRepository.existsById(query.ownerId());
    }

    /**
     * Retrieves a financial account by its ID.
     * @param query the query containing the financial account ID
     * @return an Optional containing the FinancialAccount if found, or empty if not found
     */
    @Override
    public Optional<FinancialAccount> getFinancialAccountById(GetFinancialAccountByIdQuery query) {
        return financialAccountRepository.findById(query.financialAccountId());
    }
}

package com.resolum.intiva.platform.categories.domain.services;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.queries.GetAllFinancialAccountsByOwnerId;
import com.resolum.intiva.platform.categories.domain.model.queries.GetFinancialAccountByIdQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetFinancialAccountByOwnerId;

import java.util.List;
import java.util.Optional;

/**
 *  Service interface for handling financial account-related queries.
 */
public interface FinancialAccountQueryService {

    /**
     * Get all financial accounts by owner id
     * @param query the query
     * @return the list of financial accounts
     */
    List<FinancialAccount> handle(GetAllFinancialAccountsByOwnerId query);

    /**
     * Check if a financial account exists by id
     * @param query the query containing the financial account id
     * @return true if the financial account exists, false otherwise
     */
    boolean handle(GetFinancialAccountByOwnerId query);

    /**
     * Get a financial account by id
     * @param query the query containing the financial account id
     * @return an Optional containing the financial account if found, or empty if not found
     */
    Optional<FinancialAccount> handle(GetFinancialAccountByIdQuery query);
}

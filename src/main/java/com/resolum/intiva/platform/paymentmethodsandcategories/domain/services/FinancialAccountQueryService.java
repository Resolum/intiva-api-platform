package com.resolum.intiva.platform.paymentmethodsandcategories.domain.services;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetAllFinancialAccountsByOwnerId;

import java.util.List;

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
     * @param financialAccountId the financial account id
     * @return true if the financial account exists, false otherwise
     */
    boolean existsFinancialAccountById(Long financialAccountId);
}

package com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl;

import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.resources.responses.FinancialAccountResource;

import java.math.BigDecimal;

/**
 * Context facade for financial account-related operations.
 */
public interface FinancialAccountContextFacade {

    /**
     * Check if a financial account exists by owner id
     * @param financialAccountId the financial account id
     * @return true if a financial account exists, false otherwise
     */
    boolean existsFinancialAccountById(Long financialAccountId);

    /**
     * Create a default financial account for a user
     * @param ownerId the owner id
     */
    void createDefaultFinancialAccount(Long ownerId);

    /**
     * Create a new financial account transaction for a financial account
     * @param financialAccountId the financial account id
     * @param transactionType the type of transaction (e.g., deposit, withdrawal)
     * @param amount the amount of the transaction
     * @param currencyCode the currency code of the transaction
     */
    void createFinancialAccountTransaction(Long financialAccountId, String transactionType, BigDecimal amount, String currencyCode);
}

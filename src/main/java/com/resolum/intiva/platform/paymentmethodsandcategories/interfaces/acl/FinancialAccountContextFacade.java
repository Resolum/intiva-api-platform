package com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl;

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
}

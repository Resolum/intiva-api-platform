package com.resolum.intiva.platform.categories.interfaces.acl;

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

    /**
     * Get the current amount of a financial account
     * @param financialAccountId the financial account id
     * @return the current amount of the financial account
     */
    BigDecimal getCurrentAmount(Long financialAccountId);

    /**
     * Checks whether the current amount of a financial account can cover a requested expense amount.
     *
     * @param financialAccountId the financial account id
     * @param amount the requested expense amount
     * @return true when the account balance is greater than or equal to the requested amount
     */
    boolean hasSufficientBalance(Long financialAccountId, BigDecimal amount);

    /**
     * Get a financial account by id
     * @param financialAccountId the financial account id
     * @return the financial account resource
     */
    String getFinancialAccountNameById(Long financialAccountId);
}

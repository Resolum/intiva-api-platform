package com.resolum.intiva.platform.finances.application.internal.outboundservices.acl;

import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl.FinancialAccountContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service class for interacting with external financial account services.
 */
@Service
public class FinancesExternalFinancialAccountService {

    /**
     * The FinancialAccountContextFacade is a facade that provides access to the financial account context.
     */
    private final FinancialAccountContextFacade financialAccountContextFacade;

    /**
     * Constructor for FinancesExternalFinancialAccountService.
     *
     * @param financialAccountContextFacade the FinancialAccountContextFacade to be used by this service
     */
    public FinancesExternalFinancialAccountService(FinancialAccountContextFacade financialAccountContextFacade) {
        this.financialAccountContextFacade = financialAccountContextFacade;
    }

    /**
     * Checks if a financial account exists by its ID.
     * @param financialAccountId the ID of the financial account to check
     * @return an Optional containing a boolean indicating whether the financial account exists
     */
    public Boolean existsFinancialAccountById(Long financialAccountId) {
        return financialAccountContextFacade.existsFinancialAccountById(financialAccountId);
    }

    /**
     * Creates a new financial account transaction for a specified financial account.
     * @param financialAccountId the ID of the financial account for which to create the transaction
     * @param transactionType the type of transaction (e.g., deposit, withdrawal)
     * @param amount the amount of the transaction
     * @param currencyCode the currency code of the transaction
     */
    public void createFinancialAccountTransaction(Long financialAccountId, String transactionType, BigDecimal amount, String currencyCode) {
        financialAccountContextFacade.createFinancialAccountTransaction(financialAccountId, transactionType, amount, currencyCode);
    }

    /**
     * Checks whether the referenced financial account can cover a requested expense amount.
     *
     * @param financialAccountId financial account identifier
     * @param amount requested expense amount
     * @return true when the account balance is sufficient
     */
    public Boolean hasSufficientBalance(Long financialAccountId, BigDecimal amount) {
        return financialAccountContextFacade.hasSufficientBalance(financialAccountId, amount);
    }
}

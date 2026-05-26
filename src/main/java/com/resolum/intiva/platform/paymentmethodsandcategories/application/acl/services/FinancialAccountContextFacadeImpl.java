package com.resolum.intiva.platform.paymentmethodsandcategories.application.acl.services;

import com.resolum.intiva.platform.paymentmethodsandcategories.application.internal.commandhandlers.FinancialAccountCommandServiceImpl;
import com.resolum.intiva.platform.paymentmethodsandcategories.application.internal.queryhandlers.FinancialAccountQueryServiceImpl;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl.FinancialAccountContextFacade;
import org.springframework.stereotype.Service;

/**
 * Implementation of the FinancialAccountContextFacade interface.
 * This class serves as a facade for financial account related operations, providing a simplified interface for clients.
 */
@Service
public class FinancialAccountContextFacadeImpl implements FinancialAccountContextFacade {

    /**
     * The command service for financial accounts.
     */
    private final FinancialAccountCommandServiceImpl financialAccountCommandService;

    /**
     * The query service for financial accounts.
     */
    private final FinancialAccountQueryServiceImpl financialAccountQueryService;

    /**
     * Constructor for FinancialAccountContextFacadeImpl.
     * @param financialAccountCommandService the command service for financial accounts.
     * @param financialAccountQueryService the query service for financial accounts.
     */
    public FinancialAccountContextFacadeImpl(FinancialAccountCommandServiceImpl financialAccountCommandService, FinancialAccountQueryServiceImpl financialAccountQueryService) {
        this.financialAccountCommandService = financialAccountCommandService;
        this.financialAccountQueryService = financialAccountQueryService;
    }

    /**
     * Checks if a financial account exists by its id.
     * @param financialAccountId the id of the financial account
     * @return true if the financial account exists, false otherwise
     */
    @Override
    public boolean existsFinancialAccountById(Long financialAccountId) {
        return financialAccountQueryService.existsFinancialAccountById(financialAccountId);
    }

    /**
     * Creates a default financial account for a user.
     * @param ownerId the owner id of the financial account
     */
    @Override
    public void createDefaultFinancialAccount(Long ownerId) {
        var createdDefaultFinancialAccount = new CreateDefaultFinancialAccountCommand(
                ownerId
        );
        financialAccountCommandService.handle(createdDefaultFinancialAccount);
    }
}

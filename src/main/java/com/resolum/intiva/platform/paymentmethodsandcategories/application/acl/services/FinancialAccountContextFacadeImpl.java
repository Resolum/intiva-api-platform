package com.resolum.intiva.platform.paymentmethodsandcategories.application.acl.services;

import com.resolum.intiva.platform.paymentmethodsandcategories.application.internal.commandhandlers.FinancialAccountCommandServiceImpl;
import com.resolum.intiva.platform.paymentmethodsandcategories.application.internal.queryhandlers.FinancialAccountQueryServiceImpl;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateFinancialAccountTransaction;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetFinancialAccountByIdQuery;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetFinancialAccountByOwnerId;
import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl.FinancialAccountContextFacade;
import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.assemblers.FinancialAccountResourceFromEntityAssembler;
import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.resources.responses.FinancialAccountResource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
        var getFinancialAccountByOwnerIdQuery = new GetFinancialAccountByOwnerId(
                financialAccountId
        );
        return financialAccountQueryService.existsFinancialAccountById(getFinancialAccountByOwnerIdQuery);
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

    /**
     * Creates a new financial account transaction for a financial account.
     * @param financialAccountId the financial account id
     * @param transactionType the type of transaction (e.g., deposit, withdrawal)
     * @param amount the amount of the transaction
     * @param currencyCode the currency code of the transaction
     */
    @Override
    public void createFinancialAccountTransaction(Long financialAccountId, String transactionType, BigDecimal amount, String currencyCode) {
        var createFinancialAccountTransactionCommand = new CreateFinancialAccountTransaction(
                financialAccountId,
                amount,
                currencyCode,
                transactionType
        );
        financialAccountCommandService.handle(createFinancialAccountTransactionCommand);
    }
}

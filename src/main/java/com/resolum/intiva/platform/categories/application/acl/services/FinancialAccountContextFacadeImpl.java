package com.resolum.intiva.platform.categories.application.acl.services;

import com.resolum.intiva.platform.categories.application.internal.commandhandlers.FinancialAccountCommandServiceImpl;
import com.resolum.intiva.platform.categories.application.internal.queryhandlers.FinancialAccountQueryServiceImpl;
import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateFinancialAccountTransaction;
import com.resolum.intiva.platform.categories.domain.model.queries.GetFinancialAccountByIdQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetFinancialAccountByOwnerId;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.interfaces.acl.FinancialAccountContextFacade;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Implementation of the FinancialAccountContextFacade interface.
 * This class serves as a facade for financial account related operations, providing a simplified interface for clients.
 */
@Slf4j
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
    @Transactional(readOnly = true)
    public boolean existsFinancialAccountById(Long financialAccountId) {
        log.info("ACL - Checking existence of financial account with id: {}", financialAccountId);
        var getFinancialAccountByOwnerIdQuery = new GetFinancialAccountByOwnerId(
                financialAccountId
        );
        return financialAccountQueryService.handle(getFinancialAccountByOwnerIdQuery);
    }

    /**
     * Creates a default financial account for a user.
     * @param ownerId the owner id of the financial account
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDefaultFinancialAccount(Long ownerId) {
        log.info("ACL - Creating default financial account for owner id: {}", ownerId);
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
    @Transactional(rollbackFor = Exception.class)
    public void createFinancialAccountTransaction(Long financialAccountId, String transactionType, BigDecimal amount, String currencyCode) {
        log.info("ACL - Creating financial account transaction for financial account id: {}, transaction type: {}, amount: {}, currency code: {}",
                financialAccountId, transactionType, amount, currencyCode);
        var createFinancialAccountTransactionCommand = new CreateFinancialAccountTransaction(
                financialAccountId,
                amount,
                currencyCode,
                transactionType
        );
        financialAccountCommandService.handle(createFinancialAccountTransactionCommand);
    }

    @Override
    public BigDecimal getCurrentAmount(Long financialAccountId) {
        log.info("ACL - Retrieving current amount for financial account id: {}", financialAccountId);
        var getFinancialAccountBy = new GetFinancialAccountByIdQuery(
                financialAccountId
        );
        var financialAccount = financialAccountQueryService.handle(getFinancialAccountBy);
        return financialAccount
                .map(FinancialAccount::getCurrentAmount)
                .map(Money::amount)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Verifies whether the financial account can cover a requested expense amount.
     *
     * @param financialAccountId the financial account id
     * @param amount the requested expense amount
     * @return true if the current amount is greater than or equal to the requested amount
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasSufficientBalance(Long financialAccountId, BigDecimal amount) {
        return getCurrentAmount(financialAccountId).compareTo(amount) >= 0;
    }

    /**
     * Retrieves the name of a financial account by its id.
     *
     * @param financialAccountId the financial account id
     * @return the name of the financial account, or null if not found
     */
    @Override
    @Transactional(readOnly = true)
    public String getFinancialAccountNameById(Long financialAccountId) {
        log.info("ACL - Retrieving financial account name for financial account id: {}", financialAccountId);
        var financialAccountByIdQuery = new GetFinancialAccountByIdQuery(
                financialAccountId
        );
        return financialAccountQueryService.handle(financialAccountByIdQuery)
                .map(FinancialAccount::getName)
                .map(AccountName::getName)
                .orElse(null);
    }
}

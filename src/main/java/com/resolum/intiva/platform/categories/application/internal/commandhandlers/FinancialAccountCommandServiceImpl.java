package com.resolum.intiva.platform.categories.application.internal.commandhandlers;

import com.resolum.intiva.platform.categories.domain.model.commands.CreateFinancialAccountTransaction;
import com.resolum.intiva.platform.categories.domain.model.entities.CashAccount;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.exceptions.FinancialAccountSyncConflictException;
import com.resolum.intiva.platform.categories.domain.services.FinancialAccountCommandService;
import com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.FinancialAccountRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the FinancialAccountCommandService interface.
 *
 */
@Slf4j
@Service
public class FinancialAccountCommandServiceImpl implements FinancialAccountCommandService {

    // Repository for accessing financial account data from the database
    private final FinancialAccountRepository financialAccountRepository;

    // Constructor for dependency injection of the FinancialAccountRepository
    public FinancialAccountCommandServiceImpl(FinancialAccountRepository financialAccountRepository) {
        this.financialAccountRepository = financialAccountRepository;
    }

    /**
     *  Handles the creation of a default financial account for a user.
     * @param command the command containing the financial account details
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(CreateDefaultFinancialAccountCommand command) {
        log.info(
                "{} - Creating default financial account for user with ID: {}",
                command.getClass().getSimpleName(),
                command.ownerId()
        );
        var defaultFinancialAccount = CashAccount.createDefault(command.ownerId());
        financialAccountRepository.save(defaultFinancialAccount);
    }

    /**
     * Handles the creation of a new financial account transaction.
     * @param command the command containing the financial account transaction details
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(CreateFinancialAccountTransaction command) {
        log.info(
                "{} - Creating financial account transaction for account ID {} with amount {}, currency {} and transaction type {}",
                command.getClass().getSimpleName(),
                command.financialAccountId(),
                command.amount(),
                command.currencyCode(),
                command.transactionType()
        );
        var financialAccount = financialAccountRepository.findByIdForUpdate(command.financialAccountId()).orElseThrow();
        var transactionType = TransactionTypes.fromString(command.transactionType());
        var amount = new Money(
                command.amount(),
                CurrencyCodes.fromString(command.currencyCode())
        );

        if (command.ownerType() == OwnerTypes.FAMILY
                && command.baseAccountVersion() != null
                && !command.baseAccountVersion().equals(financialAccount.getVersion())
                && transactionType == TransactionTypes.EXPENSE
                && financialAccount.getCurrentAmount().amount().compareTo(command.amount()) < 0) {
            throw new FinancialAccountSyncConflictException(
                    command.financialAccountId(),
                    command.baseAccountVersion(),
                    financialAccount.getVersion(),
                    financialAccount.getCurrentAmount().amount(),
                    command.amount()
            );
        }

        financialAccount.applyTransaction(amount, transactionType);
        financialAccountRepository.save(financialAccount);
    }
}

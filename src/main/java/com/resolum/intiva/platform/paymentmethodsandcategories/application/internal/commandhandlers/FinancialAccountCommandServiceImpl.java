package com.resolum.intiva.platform.paymentmethodsandcategories.application.internal.commandhandlers;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateFinancialAccountTransaction;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.entities.CashAccount;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.services.FinancialAccountCommandService;
import com.resolum.intiva.platform.paymentmethodsandcategories.infraestructure.persistence.jpa.repositories.FinancialAccountRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import org.springframework.stereotype.Service;

/**
 * Implementation of the FinancialAccountCommandService interface.
 *
 */
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
    public void handle(CreateDefaultFinancialAccountCommand command) {
        var defaultFinancialAccount = CashAccount.createDefault(command.ownerId());
        financialAccountRepository.save(defaultFinancialAccount);
    }

    /**
     * Handles the creation of a new financial account transaction.
     * @param command the command containing the financial account transaction details
     */
    @Override
    public void handle(CreateFinancialAccountTransaction command) {
        var financialAccount = financialAccountRepository.findById(command.financialAccountId()).orElseThrow();
        financialAccount.applyTransaction(
                new Money(
                        command.amount(),
                        CurrencyCodes.fromString(command.currencyCode())
                ),
                TransactionTypes.fromString(command.transactionType())
        );
        financialAccountRepository.save(financialAccount);
    }
}

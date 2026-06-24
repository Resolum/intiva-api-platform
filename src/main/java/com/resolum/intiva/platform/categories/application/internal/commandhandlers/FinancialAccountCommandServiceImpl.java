package com.resolum.intiva.platform.categories.application.internal.commandhandlers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateFinancialAccountTransaction;
import com.resolum.intiva.platform.categories.domain.model.commands.UpdateFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.entities.CashAccount;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.entities.CreditCardAccount;
import com.resolum.intiva.platform.categories.domain.model.entities.DebitCardAccount;
import com.resolum.intiva.platform.categories.domain.model.entities.WalletAccount;
import com.resolum.intiva.platform.categories.domain.model.exceptions.FinancialAccountSyncConflictException;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.categories.domain.services.FinancialAccountCommandService;
import com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.FinancialAccountRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class FinancialAccountCommandServiceImpl implements FinancialAccountCommandService {

    private final FinancialAccountRepository financialAccountRepository;

    public FinancialAccountCommandServiceImpl(FinancialAccountRepository financialAccountRepository) {
        this.financialAccountRepository = financialAccountRepository;
    }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancialAccount handle(CreateFinancialAccountCommand command) {
        log.info("{} - Creating financial account for user ID: {}",
                command.getClass().getSimpleName(), command.ownerId());

        var currencyCode = CurrencyCodes.fromString(command.currency());
        var initialAmount = new Money(command.initialAmount(), currencyCode);
        var accountName = new AccountName(command.name());
        String accountType = command.accountType().toUpperCase();

        FinancialAccount account = switch (accountType) {
            case "WALLET" -> {
                Institution inst = command.institution() != null && !command.institution().isBlank()
                        ? new Institution(command.institution()) : null;
                yield new WalletAccount(accountName, initialAmount, inst, command.ownerId());
            }
            case "DEBITCARD" -> {
                if (command.institution() == null || command.institution().isBlank()) {
                    throw new IllegalArgumentException("Institution is required for DEBITCARD accounts");
                }
                yield new DebitCardAccount(accountName, initialAmount,
                        new Institution(command.institution()), command.ownerId());
            }
            case "CREDITCARD" -> {
                if (command.creditLimit() == null || command.creditLimit().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Credit limit must be greater than zero for CREDITCARD accounts");
                }
                if (command.institution() == null || command.institution().isBlank()) {
                    throw new IllegalArgumentException("Institution is required for CREDITCARD accounts");
                }
                yield new CreditCardAccount(accountName, initialAmount,
                        command.creditLimit(), new Institution(command.institution()), command.ownerId());
            }
            default -> throw new IllegalArgumentException("Unknown account type: " + accountType);
        };

        return financialAccountRepository.save(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancialAccount handle(UpdateFinancialAccountCommand command) {
        log.info("{} - Updating financial account ID: {}", command.getClass().getSimpleName(), command.accountId());

        var account = financialAccountRepository.findById(command.accountId())
                .orElseThrow(() -> new RuntimeException("Financial account not found with ID: " + command.accountId()));

        if (command.name() != null) {
            account.changeName(new AccountName(command.name()));
        }
        if (command.isActive() != null) {
            if (Boolean.FALSE.equals(command.isActive())) {
                account.deactivate();
            } else {
                account.activate();
            }
        }

        return financialAccountRepository.save(account);
    }

}

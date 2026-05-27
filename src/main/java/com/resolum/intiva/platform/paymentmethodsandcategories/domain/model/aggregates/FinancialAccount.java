package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.exceptions.InactiveFinancialAccountException;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.exceptions.InsufficientFundsException;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.exceptions.InvalidTransactionAmountException;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type")
public abstract class FinancialAccount extends AuditableAbstractAggregate<FinancialAccount> {

    @Embedded
    private AccountName name;

    private Boolean isActive;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "current_amount")),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "current_currency_code"))
    })
    private Money currentAmount;

    private Long ownerId;

    // Default protected constructor for JPA.
    protected FinancialAccount() {
    }

    protected FinancialAccount(AccountName name, Money initialAmount, Long ownerId) {
        this.name = name;
        this.isActive = true;
        this.currentAmount = initialAmount;
        this.ownerId = ownerId;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void changeName(AccountName name) {
        this.name = name;
    }

    public void updateCurrentAmount(Money amount) {
        this.currentAmount = this.currentAmount.add(amount);
    }

    /**
     * Applies a transaction to the financial account, updating the current amount based on the transaction type.
     *
     * @param amount the amount of the transaction, must be greater than zero
     * @param type   the type of the transaction (INCOME or EXPENSE)
     * @throws InactiveFinancialAccountException if the account is inactive
     * @throws InvalidTransactionAmountException if the transaction amount is not greater than zero
     * @throws InsufficientFundsException        if the transaction is an expense and there are insufficient funds
     */
    public void applyTransaction(Money amount, TransactionTypes type) {

        if (!isActive) {
            throw new InactiveFinancialAccountException();
        }

        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionAmountException();
        }

        if (type == TransactionTypes.EXPENSE) {

            if (this.currentAmount.getAmount()
                    .compareTo(amount.getAmount()) < 0) {
                throw new InsufficientFundsException();
            }

            this.currentAmount = this.currentAmount.subtract(amount);
        }

        if (type == TransactionTypes.INCOME) {
            this.currentAmount = this.currentAmount.add(amount);
        }
    }
}

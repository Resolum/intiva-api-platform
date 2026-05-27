package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates;

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

    public void applyTransaction(Money amount, TransactionTypes type) {

        if (!isActive) {
            throw new IllegalStateException("Account is inactive. Cannot apply transactions.");
        }

        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }

        if (type == TransactionTypes.EXPENSE) {
            this.currentAmount = this.currentAmount.subtract(amount);
        }

        if (type == TransactionTypes.INCOME) {
            this.currentAmount = this.currentAmount.add(amount);
        }
    }
}

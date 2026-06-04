package com.resolum.intiva.platform.categories.domain.model.entities;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
@DiscriminatorValue("CREDIT_CARD")
public class CreditCardAccount extends FinancialAccount {

    @Embedded
    private Institution institution;

    @Column(precision = 19, scale = 2)
    private BigDecimal creditLimit;

    protected CreditCardAccount() {}

    public CreditCardAccount(AccountName name, Money initialAmount,
                             BigDecimal creditLimit, Institution institution, Long ownerId) {
        super(name, initialAmount, ownerId);
        this.institution = institution;
        this.creditLimit = creditLimit;
    }

    @Override
    public void applyTransaction(Money amount, TransactionTypes type) {

        if (!getIsActive()) {
            throw new IllegalStateException("Account is inactive. Cannot apply transactions.");
        }

        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be greater than zero."
            );
        }

        if (type == TransactionTypes.EXPENSE) {

            var newDebt = getCurrentAmount().add(amount);

            if (newDebt.getAmount().compareTo(creditLimit) > 0) {
                throw new IllegalStateException("Credit limit exceeded.");
            }

            updateCurrentAmount(newDebt);
        }

        if (type == TransactionTypes.INCOME) {

            updateCurrentAmount(getCurrentAmount().subtract(amount));
        }
    }
}

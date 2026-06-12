package com.resolum.intiva.platform.categories.domain.model.entities;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CASH")
public class CashAccount extends FinancialAccount {

    protected CashAccount() {}

    public CashAccount(Long ownerId, CurrencyCodes currency) {
        super(
                new AccountName("Efectivo"),
                new Money(BigDecimal.ZERO, currency),
                ownerId
        );
    }

    public static CashAccount createDefault(Long ownerId) {
        return new CashAccount(ownerId, CurrencyCodes.PEN);
    }
}

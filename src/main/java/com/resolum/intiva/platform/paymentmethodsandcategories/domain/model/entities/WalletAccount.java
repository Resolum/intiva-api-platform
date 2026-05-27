package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.entities;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("WALLET")
public class WalletAccount extends FinancialAccount {

    @Embedded
    private Institution institution;

    protected WalletAccount() {}

    public WalletAccount(AccountName name, Money initialAmount,
                         Institution institution, Long ownerId) {
        super(name, initialAmount, ownerId);
        this.institution = institution;
    }
}

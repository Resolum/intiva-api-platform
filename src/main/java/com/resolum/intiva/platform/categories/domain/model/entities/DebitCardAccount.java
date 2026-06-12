package com.resolum.intiva.platform.categories.domain.model.entities;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
@DiscriminatorValue("DEBITCARD")
public class DebitCardAccount extends FinancialAccount {

    @Embedded
    private Institution institution;

    protected DebitCardAccount() {}

    public DebitCardAccount(AccountName name, Money initialAmount,
                            Institution institution, Long ownerId) {
        super(name, initialAmount, ownerId);
        this.institution = institution;
    }
}

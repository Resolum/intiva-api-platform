package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects.Institution;
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

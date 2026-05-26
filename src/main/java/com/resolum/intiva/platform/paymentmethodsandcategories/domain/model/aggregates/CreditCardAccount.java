package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
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
}

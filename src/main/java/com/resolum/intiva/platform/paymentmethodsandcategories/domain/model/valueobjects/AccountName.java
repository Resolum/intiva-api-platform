package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record AccountName(String accountName) {

    public AccountName(String accountName) {
        if (accountName.isBlank())
            throw new IllegalArgumentException("Account name cannot be blank");
        if (accountName.length() > 50)
            throw new IllegalArgumentException("Account name cannot be longer than 50 characters");
        this.accountName = accountName;
    }

    public String getName() {
        return accountName;
    }
}

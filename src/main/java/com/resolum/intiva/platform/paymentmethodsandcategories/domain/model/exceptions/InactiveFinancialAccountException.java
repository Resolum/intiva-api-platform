package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.exceptions;

public class InactiveFinancialAccountException extends RuntimeException {

    public InactiveFinancialAccountException() {
        super("Financial account is inactive. Transactions cannot be applied.");
    }
}

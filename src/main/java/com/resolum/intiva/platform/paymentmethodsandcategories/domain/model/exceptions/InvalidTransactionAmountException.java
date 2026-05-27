package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.exceptions;

public class InvalidTransactionAmountException extends RuntimeException {

    public InvalidTransactionAmountException() {
        super("Transaction amount must be greater than zero.");
    }
}

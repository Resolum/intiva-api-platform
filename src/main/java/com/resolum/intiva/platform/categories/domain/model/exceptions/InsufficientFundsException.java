package com.resolum.intiva.platform.categories.domain.model.exceptions;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("Insufficient funds for this transaction.");
    }
}

package com.resolum.intiva.platform.categories.domain.model.exceptions;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("No se pudo registrar el gasto. No cuenta con saldo suficiente");
    }
}

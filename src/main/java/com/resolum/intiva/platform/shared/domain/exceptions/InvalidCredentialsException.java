package com.resolum.intiva.platform.shared.domain.exceptions;

/**
 * Exception thrown when invalid credentials are provided during authentication.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
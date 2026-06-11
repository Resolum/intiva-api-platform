package com.resolum.intiva.platform.household.domain.exceptions;

public class UserAlreadyMemberException extends RuntimeException {
    public UserAlreadyMemberException(String message) {
        super(message);
    }
}

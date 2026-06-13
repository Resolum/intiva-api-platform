package com.resolum.intiva.platform.household.domain.exceptions;

public class InvitationExpiredException extends RuntimeException {
    public InvitationExpiredException(String message) {
        super(message);
    }
}

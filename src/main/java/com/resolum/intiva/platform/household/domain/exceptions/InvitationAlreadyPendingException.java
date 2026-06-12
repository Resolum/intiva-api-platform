package com.resolum.intiva.platform.household.domain.exceptions;

public class InvitationAlreadyPendingException extends RuntimeException {
    public InvitationAlreadyPendingException(String message) {
        super(message);
    }
}

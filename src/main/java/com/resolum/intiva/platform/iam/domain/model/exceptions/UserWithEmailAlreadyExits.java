package com.resolum.intiva.platform.iam.domain.model.exceptions;


/**
 * Exception thrown when a user with the given email already exists.
 */
public class UserWithEmailAlreadyExits extends RuntimeException {

    /**
     * Constructor for UserWithEmailAlreadyExits.
     * @param email the email of the user that already exists
     */
    public UserWithEmailAlreadyExits(String email) {
        super(String.format("User with email %s already exists.", email));
    }
}

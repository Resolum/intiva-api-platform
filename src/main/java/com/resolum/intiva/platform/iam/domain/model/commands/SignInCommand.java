package com.resolum.intiva.platform.iam.domain.model.commands;

import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.PasswordHash;

/**
 * Command for signing in a user.
 * @param email the email of the user to sign in
 * @param password the password of the user to sign in
 */
public record SignInCommand(Email email, PasswordHash password) {

    /**
     * Constructor for SignInCommand.
     * @param email the email of the user to sign in
     * @param password the password of the user to sign in
      * @throws IllegalArgumentException if email or password is null or blank
     */
    public SignInCommand {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password cannot be null");
        }

        if (email.getValue().isBlank() || password.getValue().isBlank()) {
            throw new IllegalArgumentException("Email and password cannot be blank");
        }
    }
}

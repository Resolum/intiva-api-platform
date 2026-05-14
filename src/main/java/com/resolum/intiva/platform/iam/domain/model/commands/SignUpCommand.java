package com.resolum.intiva.platform.iam.domain.model.commands;

import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;

/**
 * Command for signing up a new user.
 *
 * @param email the email of the user to sign up
 * @param password the password of the user to sign up
 */
public record SignUpCommand(Email email, String password) {
}

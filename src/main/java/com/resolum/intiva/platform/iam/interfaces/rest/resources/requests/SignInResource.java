package com.resolum.intiva.platform.iam.interfaces.rest.resources.requests;

/**
 * Resource for sign-in request.
 * @param email the email of the user who wants to sign in.
 * @param password the password of the user who wants to sign in.
 */
public record SignInResource(String email, String password) {
}

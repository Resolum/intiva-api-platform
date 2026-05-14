package com.resolum.intiva.platform.iam.interfaces.rest.resources.requests;

/**
 * Resource for sign up request.
 * This resource is received from the client when the user wants to sign up to the system.
 *
 * @param email the email of the user who wants to sign up to the system.
 * @param password the password of the user who wants to sign up to the system.
 */
public record SignUpResource(String email, String password) {
}

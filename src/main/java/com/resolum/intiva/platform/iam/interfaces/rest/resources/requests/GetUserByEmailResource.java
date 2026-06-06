package com.resolum.intiva.platform.iam.interfaces.rest.resources.requests;

/**
 * Resource for get user by email request.
 * This resource is received from the client when the user wants to get information of a user by email.
 * @param email the email of the user who wants to get a user by email.
 */
public record GetUserByEmailResource(String email) {
}

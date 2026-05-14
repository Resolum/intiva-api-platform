package com.resolum.intiva.platform.iam.interfaces.rest.resources.responses;

/**
 * Resource for sign-in response.
 * @param userId the id of the user who signed in.
 * @param email the email of the user who signed in.
 * @param token the JWT token of the user who signed in.
 */
public record AuthenticatedUserResource(Long userId, String email, String token) {
}

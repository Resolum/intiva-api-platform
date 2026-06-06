package com.resolum.intiva.platform.iam.interfaces.rest.resources.responses;

/**
 * UserResource is a record that represents a user resource in the REST API. It contains the user's id and email.
 * @param id the user's id
 * @param email the user's email
 */
public record UserResource(Long id, String email) {
}

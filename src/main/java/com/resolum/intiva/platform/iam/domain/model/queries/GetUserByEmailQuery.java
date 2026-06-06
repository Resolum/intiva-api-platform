package com.resolum.intiva.platform.iam.domain.model.queries;

import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;

/**
 * Query to get a user by their email address.
 * @param email The email address of the user to retrieve.
 */
public record GetUserByEmailQuery(Email email) {
}

package com.resolum.intiva.platform.iam.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Query to get a user by their ID.
 * @param userId The ID of the user to retrieve.
 */
public record GetUserByIdQuery(UserId userId) {
}

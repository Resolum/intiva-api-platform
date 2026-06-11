package com.resolum.intiva.platform.profiles.domain.model.queries;

/**
 * Query to retrieve a user profile by the associated user identifier.
 *
 * <p>This query is used by both the application layer and the ACL facade to fetch
 * profile information for a given user id.</p>
 *
 * @param userId the identifier of the user whose profile is being requested
 */
public record GetProfileByUserIdQuery(Long userId) {
}

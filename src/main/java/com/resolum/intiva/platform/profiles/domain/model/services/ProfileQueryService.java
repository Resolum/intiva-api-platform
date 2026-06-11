package com.resolum.intiva.platform.profiles.domain.model.services;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Profile;
import com.resolum.intiva.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;

import java.util.Optional;

/**
 * Service interface for handling profile-related queries, such as retrieving a profile
 * by its associated user identifier.
 */
public interface ProfileQueryService {
    /**
     * Handles the query to retrieve a profile by its associated user id.
     *
     * @param query the query containing the user identifier
     * @return an Optional containing the Profile if found, or empty if no profile exists for that user
     */
    Optional<Profile> handle(GetProfileByUserIdQuery query);
}

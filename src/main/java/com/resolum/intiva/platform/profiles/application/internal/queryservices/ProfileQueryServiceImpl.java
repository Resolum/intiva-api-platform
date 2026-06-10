package com.resolum.intiva.platform.profiles.application.internal.queryservices;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Profile;
import com.resolum.intiva.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.resolum.intiva.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import com.resolum.intiva.platform.profiles.domain.model.services.ProfileQueryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the ProfileQueryService that retrieves profile data from the database.
 *
 * <p>This service delegates to the ProfileRepository to perform queries and returns
 * the resulting Profile aggregate wrapped in an Optional.</p>
 */
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Retrieves a profile by its associated user identifier.
     *
     * @param query the query containing the user identifier
     * @return an Optional containing the Profile if found, or empty if no profile exists for that user
     */
    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId_UserId(query.userId());
    }
}

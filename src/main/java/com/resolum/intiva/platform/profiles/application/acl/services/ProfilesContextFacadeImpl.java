package com.resolum.intiva.platform.profiles.application.acl.services;

import com.resolum.intiva.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.resolum.intiva.platform.profiles.domain.model.services.ProfileQueryService;
import com.resolum.intiva.platform.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ProfilesContextFacade that provides a simplified interface for
 * external bounded contexts to consume profile information.
 *
 * <p>This facade follows the ACL (Anti-Corruption Layer) pattern, decoupling external consumers
 * from the profiles domain internals. It delegates to the ProfileQueryService to fetch data.</p>
 */
@Service
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final ProfileQueryService profileQueryService;

    public ProfilesContextFacadeImpl(ProfileQueryService profileQueryService) {
        this.profileQueryService = profileQueryService;
    }

    /**
     * Retrieves the display name of a user's profile.
     *
     * @param userId the identifier of the user whose profile name is being requested
     * @return the profile display name, or an empty string if no profile was found
     */
    @Override
    public String getProfileName(Long userId) {
        var profile = profileQueryService.handle(new GetProfileByUserIdQuery(userId));
        return profile.map(p -> p.getName()).orElse("");
    }
}

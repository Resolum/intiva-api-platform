package com.resolum.intiva.platform.profiles.application.acl.services;

import com.resolum.intiva.platform.profiles.domain.model.commands.CreateUserOnboardingCommand;
import com.resolum.intiva.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.resolum.intiva.platform.profiles.domain.model.services.OnboardingCommandService;
import com.resolum.intiva.platform.profiles.domain.model.services.ProfileQueryService;
import com.resolum.intiva.platform.profiles.interfaces.acl.ProfilesContextFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ProfilesContextFacade that provides a simplified interface for
 * external bounded contexts to consume profile information.
 *
 * <p>This facade follows the ACL (Anti-Corruption Layer) pattern, decoupling external consumers
 * from the profiles domain internals. It delegates to the ProfileQueryService to fetch data.</p>
 */
@Slf4j
@Service
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final ProfileQueryService profileQueryService;
    private final OnboardingCommandService onboardingCommandService;

    public ProfilesContextFacadeImpl(
            ProfileQueryService profileQueryService,
            OnboardingCommandService onboardingCommandService
    ) {
        this.profileQueryService = profileQueryService;
        this.onboardingCommandService = onboardingCommandService;
    }

    /**
     * Retrieves the display name of a user's profile.
     *
     * @param userId the identifier of the user whose profile name is being requested
     * @return the profile display name, or an empty string if no profile was found
     */
    @Override
    public String getProfileName(Long userId) {
        log.debug("Querying profile name for userId={}", userId);
        var profile = profileQueryService.handle(new GetProfileByUserIdQuery(userId));
        var name = profile.map(p -> p.getName()).orElse("");
        if (name.isEmpty()) {
            log.warn("Profile name not found for userId={}", userId);
        }
        return name;
    }

    /**
     * Creates the onboarding tutorial state for a newly registered user.
     */
    @Override
    public void createUserOnboarding(Long userId) {
        log.info("Creating onboarding for userId={} through Profiles ACL", userId);
        onboardingCommandService.handle(new CreateUserOnboardingCommand(userId));
    }
}

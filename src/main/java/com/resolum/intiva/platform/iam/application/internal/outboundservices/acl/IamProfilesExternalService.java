package com.resolum.intiva.platform.iam.application.internal.outboundservices.acl;

import com.resolum.intiva.platform.profiles.interfaces.acl.ProfilesContextFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * IamProfilesExternalService is a service that acts as an adapter to the ProfilesContextFacade, allowing the IAM bounded context to interact with the profiles context without being tightly coupled to its internal implementation.
 *
 * This service follows the ACL (Anti-Corruption Layer) pattern, providing a simplified interface for external bounded contexts to consume profile-related functionalities. It delegates calls to the ProfilesContextFacade, which handles the actual logic of interacting with the profiles domain.
 */
@Slf4j
@Service
public class IamProfilesExternalService {

    /** The ProfilesContextFacade is a service that provides methods to interact with the profiles context, such as retrieving profile information and creating onboarding states for users. */
    private final ProfilesContextFacade profilesContextFacade;

    /**
     * Constructor for IamProfilesExternalService.
     *
     * @param profilesContextFacade the ProfilesContextFacade to be used by this service
     */
    public IamProfilesExternalService (ProfilesContextFacade profilesContextFacade) {
        this.profilesContextFacade = profilesContextFacade;
    }

    /**
     * Retrieves the profile name for a given user ID by delegating to the ProfilesContextFacade.
     *
     * @param userId the identifier of the user whose profile name is being requested
     */
    public void createUserOnboarding(Long userId) {
        log.info("Creating onboarding for userId={}", userId);
        profilesContextFacade.createUserOnboarding(userId);
    }
}

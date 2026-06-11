package com.resolum.intiva.platform.profiles.interfaces.acl;

/**
 * Facade interface that exposes profile-related functionality to other bounded contexts.
 *
 * <p>This interface defines the contract for the anti-corruption layer (ACL) of the profiles
 * bounded context, allowing external modules to retrieve profile information without coupling
 * to the internal domain model.</p>
 */
public interface ProfilesContextFacade {
    /**
     * Retrieves the display name of a user's profile.
     *
     * @param userId the identifier of the user whose profile name is being requested
     * @return the profile display name, or an empty string if no profile was found
     */
    String getProfileName(Long userId);
}

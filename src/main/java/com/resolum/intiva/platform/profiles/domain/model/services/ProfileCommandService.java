package com.resolum.intiva.platform.profiles.domain.model.services;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Profile;
import com.resolum.intiva.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.resolum.intiva.platform.profiles.domain.model.commands.UpdateProfileCommand;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Service interface for handling profile-related commands such as creating a profile,
 * updating personal information, and managing avatar uploads.
 *
 * <p>Each method receives a command object or relevant parameters and returns the
 * resulting Profile aggregate, applying the necessary business rules and validations.</p>
 */
public interface ProfileCommandService {
    /**
     * Handles the creation of a new profile for a newly registered user.
     *
     * @param command the command containing userId and initial display name
     * @return the persisted Profile aggregate
     */
    Profile handle(CreateProfileCommand command);

    /**
     * Handles the update of an existing profile's personal information.
     *
     * @param command the command containing the profile field changes
     * @return an Optional containing the updated Profile if found, or empty if the user does not exist
     */
    Optional<Profile> handle(UpdateProfileCommand command);

    /**
     * Handles the upload or replacement of a profile avatar image.
     *
     * @param userId the identifier of the user whose avatar is being updated
     * @param file   the new avatar image file uploaded by the user
     * @return an Optional containing the updated Profile if found, or empty if the user does not exist
     */
    Optional<Profile> handleAvatarUpdate(Long userId, MultipartFile file);
}

package com.resolum.intiva.platform.profiles.application.internal.commandservices;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Profile;
import com.resolum.intiva.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.resolum.intiva.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.resolum.intiva.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import com.resolum.intiva.platform.profiles.domain.model.services.ProfileCommandService;
import com.resolum.intiva.platform.shared.application.internal.outboundservices.filestorage.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the ProfileCommandService interface that handles profile-related commands.
 *
 * <p>This service is responsible for processing profile creation, personal information updates,
 * and avatar image uploads. It coordinates with the ProfileRepository for persistence and the
 * ImageService for cloud-based image storage operations.</p>
 */
@Slf4j
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;
    private final ImageService imageService;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository, ImageService imageService) {
        this.profileRepository = profileRepository;
        this.imageService = imageService;
    }

    /**
     * Creates a new profile for a newly registered user.
     * Called automatically by the UserRegisteredEventHandler when a UserRegisteredEvent is received.
     *
     * @param command the command containing the userId and initial display name
     * @return the persisted Profile
     */
    @Override
    public Profile handle(CreateProfileCommand command) {
        log.info("Creating profile for userId={}", command.userId());
        var profile = Profile.builder()
                .userId(command.userId())
                .name(command.name())
                .age(0)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();
        var saved = profileRepository.save(profile);
        log.info("Profile created with id={} for userId={}", saved.getId(), command.userId());
        return saved;
    }

    /**
     * Updates the personal information of an existing profile.
     *
     * @param command the command containing the updated profile fields
     * @return an Optional containing the updated Profile if the user exists, or empty otherwise
     */
    @Override
    public Optional<Profile> handle(UpdateProfileCommand command) {
        log.debug("Updating profile for userId={}", command.userId());
        var profile = profileRepository.findByUserId_UserId(command.userId());
        if (profile.isEmpty()) {
            log.warn("Profile not found for userId={}", command.userId());
            return Optional.empty();
        }
        var profileToUpdate = profile.get();
        profileToUpdate.updatePersonalInfo(command.name(), command.bio(), command.phoneNumber(), command.age());
        var saved = profileRepository.save(profileToUpdate);
        log.info("Profile updated for userId={}", command.userId());
        return Optional.of(saved);
    }

    /**
     * Handles the upload or replacement of a profile avatar image.
     *
     * <p>If the profile already has a custom avatar (non-default), the previous image is deleted
     * from the cloud storage before uploading the new one. The new image URL and public ID are
     * persisted on the profile aggregate.</p>
     *
     * @param userId the identifier of the user whose avatar is being updated
     * @param file   the new avatar image file uploaded by the user
     * @return an Optional containing the updated Profile if the user exists, or empty otherwise
     */
    @Override
    public Optional<Profile> handleAvatarUpdate(Long userId, MultipartFile file) {
        log.info("Updating avatar for userId={}", userId);
        var profile = profileRepository.findByUserId_UserId(userId);
        if (profile.isEmpty()) {
            log.warn("Profile not found for userId={}", userId);
            return Optional.empty();
        }
        var profileToUpdate = profile.get();

        try {
            if (!profileToUpdate.hasDefaultAvatar() && profileToUpdate.getAvatarUrl() != null) {
                log.debug("Deleting previous avatar for userId={}", userId);
                imageService.delete(profileToUpdate.getAvatarUrl().publicId());
            }

            Map<String, String> uploadResult = imageService.upload(file.getBytes(), file.getOriginalFilename());
            String newUrl = uploadResult.get("url");
            String newPublicId = uploadResult.get("publicId");
            profileToUpdate.updateAvatar(newUrl, newPublicId);

            var saved = profileRepository.save(profileToUpdate);
            log.info("Avatar updated for userId={}", userId);
            return Optional.of(saved);
        } catch (IOException e) {
            log.error("Failed to read image file for userId={}", userId, e);
            throw new RuntimeException("Failed to read image file", e);
        }
    }
}

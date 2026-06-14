package com.resolum.intiva.platform.iam.application.internal.commandhandlers;

import com.resolum.intiva.platform.iam.application.internal.outboundservices.HashingService;
import com.resolum.intiva.platform.iam.application.internal.outboundservices.TokenService;
import com.resolum.intiva.platform.iam.domain.model.exceptions.UserWithEmailAlreadyExits;
import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import com.resolum.intiva.platform.iam.domain.model.commands.SignInCommand;
import com.resolum.intiva.platform.iam.domain.model.commands.SignUpCommand;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.PasswordHash;
import com.resolum.intiva.platform.iam.domain.services.UserCommandService;
import com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.resolum.intiva.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import com.resolum.intiva.platform.shared.application.internal.outboundservices.filestorage.ImageService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * UserCommandServiceImpl is the implementation of the UserCommandService interface that handles user-related commands such as sign-up.
 * It interacts with the UserRepository to perform database operations, uses HashingService to securely hash user passwords, and utilizes TokenService to generate authentication tokens for users.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    // Logger for logging important events and errors in the user command service
    private final Logger LOGGER = LoggerFactory.getLogger(UserCommandServiceImpl.class);

    // UserRepository is used to interact with the database for user-related operations
    private final UserRepository userRepository;

    // HashingService is used to hash user passwords securely before storing them in the database
    private final HashingService hashingService;

    // TokenService is used to generate authentication tokens for users after successful sign-up or login
    private final TokenService tokenService;

    // ImageService for uploading avatar images
    private final ImageService imageService;

    // ProfileRepository for updating profile data with sign-up information
    private final ProfileRepository profileRepository;

    // Constructor injection for dependencies
    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService,
            ImageService imageService,
            ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.imageService = imageService;
        this.profileRepository = profileRepository;
    }

    /**
     * Handles the sign-up command to create a new user.
     *
     * @param command The sign-up command containing user registration details.
     * @return An Optional containing the created User if successful, or empty if the operation failed (e.g., due to validation errors or existing user).
     */
    @Override
    public Optional<User> handle(SignUpCommand command) {
        if(userRepository.existsUserByEmail_Email(command.email().getValue())) {
            throw new UserWithEmailAlreadyExits(command.email().getValue());
        }

        if(!isPasswordValid(command.password())) {
            throw new IllegalArgumentException("Password does not meet security requirements.");
        }

        try {
            var email = command.email();
            var hashedPassword = hashingService.encode(command.password());
            var user = new User(command.email(), new PasswordHash(hashedPassword));
            userRepository.save(user);

            var savedUser = userRepository.findUserByEmail_Email(email.getValue());
            if (savedUser.isEmpty()) {
                LOGGER.error("User not found after save for email {}", email.getValue());
                return Optional.empty();
            }

            var userId = savedUser.get().getId();

            // Upload avatar if provided
            String avatarUrl = null;
            String avatarPublicId = null;
            if (command.avatarFile() != null && !command.avatarFile().isEmpty()) {
                try {
                    Map<String, String> uploadResult = imageService.upload(
                            command.avatarFile().getBytes(),
                            command.avatarFile().getOriginalFilename());
                    avatarUrl = uploadResult.get("url");
                    avatarPublicId = uploadResult.get("publicId");
                } catch (IOException e) {
                    LOGGER.error("Failed to read avatar file for user {}: {}", email.getValue(), e.getMessage());
                }
            }

            // Update profile with sign-up data
            var profileOpt = profileRepository.findByUserId_UserId(userId);
            if (profileOpt.isPresent()) {
                var profile = profileOpt.get();
                profile.updatePersonalInfo(
                        command.name(),
                        command.bio(),
                        command.phoneNumber(),
                        command.age()
                );
                if (avatarUrl != null) {
                    profile.updateAvatar(avatarUrl, avatarPublicId);
                }
                profileRepository.save(profile);
                LOGGER.info("Profile updated for userId={} with sign-up data", userId);
            } else {
                LOGGER.warn("Profile not found for userId={} after sign-up", userId);
            }

            LOGGER.info("User with email {} has been registered", email.getValue());
            LOGGER.info("User with email {} has signed-up", email.getValue());

            return savedUser;
        } catch (Exception e) {
            LOGGER.error("Error occurred while signing up user with email {}: {}", command.email(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Handles the sign-in command to authenticate a user.
     * @param command The sign-in command containing user authentication details.
     * @return a pair of the authenticated user and a JWT token if successful, or empty if authentication failed.
     */
    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findUserByEmail_Email(command.email().getValue());
        if (user.isEmpty())
            throw new RuntimeException("User not found");
        if (!hashingService.matches(command.password().getValue(), user.get().getPasswordHash().getValue()))
            throw new RuntimeException("Invalid password");
        var token = tokenService.generateToken(user.get().getEmail().email());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    /**
     * Validates the password against security requirements such as length, presence of uppercase and lowercase letters, digits, and special characters.
     *
     * @param password The password to validate.
     * @return true if the password meets all security requirements, false otherwise.
     */
    private boolean isPasswordValid(String password) {
        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;':\",.<>/?".indexOf(ch) >= 0);
        boolean isValidLength = password.length() >= 8;

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar && isValidLength;
    }
}

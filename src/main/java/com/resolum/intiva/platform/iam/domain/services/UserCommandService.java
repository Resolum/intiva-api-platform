package com.resolum.intiva.platform.iam.domain.services;

import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import com.resolum.intiva.platform.iam.domain.model.commands.SignUpCommand;

import java.util.Optional;

/**
 * Service interface for handling user-related commands, such as sign-up operations.
 * This service is responsible for processing commands that modify user data, ensuring that business rules and validations are applied correctly.
 */
public interface UserCommandService {

    /**
     * Handles the sign-up command to create a new user.
     * @param command The sign-up command containing user registration details.
     * @return An Optional containing the created User if successful, or empty if the operation failed (e.g., due to validation errors or existing user).
     */
    Optional<User> handle(SignUpCommand command);
}

package com.resolum.intiva.platform.iam.domain.services;

import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import com.resolum.intiva.platform.iam.domain.model.commands.SignInCommand;
import com.resolum.intiva.platform.iam.domain.model.commands.SignUpCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;

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

    /**
     * Handles the sign-in command to authenticate a user.
     * @param command The sign-in command containing user authentication details.
     * @return An Optional containing the authenticated User if successful, or empty if authentication failed (e.g., due to incorrect credentials).
     */
    Optional<ImmutablePair<User, String>> handle(SignInCommand command);
}

package com.resolum.intiva.platform.iam.application.internal.queryhandlers;

import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import com.resolum.intiva.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.resolum.intiva.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.resolum.intiva.platform.iam.domain.services.UserQueryService;
import com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * UserQueryServiceImpl is an implementation of the UserQueryService interface that provides methods to handle queries related to user retrieval.
 * It uses the UserRepository to access the user data store and perform the necessary queries to retrieve user information based on the provided criteria (such as user ID or email).
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    // The UserRepository is injected to allow access to the user data store for query operations.
    private final UserRepository userRepository;

    // Constructor for UserQueryServiceImpl that takes a UserRepository as a parameter and assigns it to the class field.
    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Handles the query to retrieve a user by their unique identifier.
     *
     * @param query The query containing the unique identifier of the user to retrieve.
     * @return An Optional containing the User if found, or empty if no user with the given identifier exists.
     */
    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId().getValue());
    }

    /**
     * Handles the query to retrieve a user by their email address.
     *
     * @param query The query containing the email address of the user to retrieve.
     * @return An Optional containing the User if found, or empty if no user with the given email exists.
     */
    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findUserByEmail_Email(query.email().getValue());
    }
}

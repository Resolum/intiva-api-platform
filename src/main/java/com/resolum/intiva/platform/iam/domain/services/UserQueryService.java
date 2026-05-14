package com.resolum.intiva.platform.iam.domain.services;

import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import com.resolum.intiva.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.resolum.intiva.platform.iam.domain.model.queries.GetUserByIdQuery;

import java.util.Optional;

/**
 * Service interface for handling user-related queries, such as retrieving user information based on specific criteria.
 * This service is responsible for processing queries that fetch user data, ensuring that the appropriate data retrieval logic is applied and that the results are returned in a consistent manner.
 */
public interface UserQueryService {

    /**
     * Handles the query to retrieve a user by their unique identifier.
     * @param query The query containing the unique identifier of the user to retrieve.
     * @return An Optional containing the User if found, or empty if no user with the given identifier exists.
     */
    Optional<User> handle(GetUserByIdQuery query);

    /**
     * Handles the query to retrieve a user by their email address.
     * @param query The query containing the email address of the user to retrieve.
     * @return An Optional containing the User if found, or empty if no user with the given email exists.
     */
    Optional<User> handle(GetUserByEmailQuery query);
}

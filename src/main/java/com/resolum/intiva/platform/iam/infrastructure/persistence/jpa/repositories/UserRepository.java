package com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities in the database.
 * This interface extends JpaRepository, providing CRUD operations and custom query methods for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * @param email The email address to search for.
     * @return An Optional containing the User if found, or empty if not found.
     */
    Optional<User> findUserByEmail(Email email);

    /**
     * Checks if a user with the given email exists in the database.
     * @param email The email to check for existence.
     * @return true if a user with the given email exists, false otherwise.
     */
    boolean existsUserByEmail(Email email);
}

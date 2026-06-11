package com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
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
     * Uses property path navigation (email_email) to resolve the @Embedded Email value object.
     * @param email The raw email string to search for.
     * @return An Optional containing the User if found, or empty if not found.
     */
    Optional<User> findUserByEmail_Email(String email);

    /**
     * Checks if a user with the given email exists in the database.
     * Uses property path navigation (email_email) to resolve the @Embedded Email value object.
     * @param email The raw email string to check for existence.
     * @return true if a user with the given email exists, false otherwise.
     */
    boolean existsUserByEmail_Email(String email);
}

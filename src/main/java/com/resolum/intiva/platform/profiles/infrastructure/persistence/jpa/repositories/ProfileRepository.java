package com.resolum.intiva.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Profile entities in the database.
 *
 * <p>This interface extends JpaRepository to provide standard CRUD operations
 * and defines custom query methods for retrieving profiles by user identifier.</p>
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    /**
     * Finds a profile by its associated user identifier.
     *
     * @param userId the user identifier to search for
     * @return an Optional containing the Profile if found, or empty if no profile exists for that user
     */
    Optional<Profile> findByUserId_UserId(@Param("userId") Long userId);
}

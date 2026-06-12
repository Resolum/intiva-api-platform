package com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.household.domain.model.aggregates.Family;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for managing Family aggregates.
 */
@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {

    /**
     * Finds all family groups owned by a specific user.
     *
     * @param ownerId the UserId value object of the owner
     * @return a list of family groups belonging to the owner
     */
    List<Family> findByOwnerId(UserId ownerId);

    /**
     * Finds a family group by its ID and owner.
     *
     * @param id      the family group identifier
     * @param ownerId the UserId value object of the owner
     * @return the matching family group if found
     */
    Optional<Family> findByIdAndOwnerId(Long id, UserId ownerId);
}

package com.resolum.intiva.platform.savings.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.savings.domain.model.aggregates.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for managing SavingGoal entities in the database.
 */
@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {

    /**
     * Finds all saving goals owned by a specific user.
     *
     * @param actorUserId the user ID to search for
     * @return a list of matching saving goals
     */
    List<SavingGoal> findAllByActorUserId(Long actorUserId);

    /**
     * Finds all saving goals owned by a specific group or family.
     *
     * @param ownerId the group or owner ID to search for
     * @return a list of matching saving goals
     */
    List<SavingGoal> findAllByOwnerId(String ownerId);
}

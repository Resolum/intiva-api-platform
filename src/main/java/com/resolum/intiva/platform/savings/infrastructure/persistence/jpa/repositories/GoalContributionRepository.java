package com.resolum.intiva.platform.savings.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.savings.domain.model.entities.GoalContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for managing GoalContribution entities in the database.
 */
@Repository
public interface GoalContributionRepository extends JpaRepository<GoalContribution, Long> {
}

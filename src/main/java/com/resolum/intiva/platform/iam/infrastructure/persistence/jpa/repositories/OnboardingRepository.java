package com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.iam.domain.model.aggregates.Onboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for the Onboarding aggregate.
 */
@Repository
public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {

    /**
     * Finds an onboarding by the user ID.
     * @param userId the ID of the user
     * @return an Optional containing the onboarding if found, or empty if not found
     */
    Optional<Onboarding> findByUserId(Long userId);
}

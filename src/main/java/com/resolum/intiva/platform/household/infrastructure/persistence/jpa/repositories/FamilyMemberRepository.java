package com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyMemberStatus;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyRole;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for managing FamilyMember aggregates.
 */
@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    /**
     * Finds all members belonging to a family group.
     *
     * @param familyId the family group identifier
     * @return a list of all members in the group
     */
    List<FamilyMember> findByFamilyId(Long familyId);

    /**
     * Finds all members of a family group filtered by status.
     *
     * @param familyId the family group identifier
     * @param status   the membership status to filter by
     * @return a list of members matching the given status
     */
    List<FamilyMember> findByFamilyIdAndStatus(Long familyId, FamilyMemberStatus status);

    /**
     * Finds a specific member by family group and user identifier.
     *
     * @param familyId the family group identifier
     * @param userId   the UserId value object of the user
     * @return the matching member if found
     */
    Optional<FamilyMember> findByFamilyIdAndUserId(Long familyId, UserId userId);

    /**
     * Finds a specific member by their member ID within a family group.
     *
     * @param id       the member identifier
     * @param familyId the family group identifier
     * @return the matching member if found
     */
    Optional<FamilyMember> findByIdAndFamilyId(Long id, Long familyId);

    /**
     * Checks whether a user is already a member of a family group.
     *
     * @param familyId the family group identifier
     * @param userId   the UserId value object of the user
     * @return true if the user is a member of the group
     */
    boolean existsByFamilyIdAndUserId(Long familyId, UserId userId);

    /**
     * Counts members with a specific role and status in a family group.
     *
     * @param familyId the family group identifier
     * @param role     the role to count
     * @param status   the status to filter by
     * @return the number of members matching the criteria
     */
    long countByFamilyIdAndRoleAndStatus(Long familyId, FamilyRole role, FamilyMemberStatus status);
}

package com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.domain.model.valueobjects.InvitationStatus;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository for managing Invitation aggregates.
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    /**
     * Finds an invitation by its unique token.
     *
     * @param token the invitation token
     * @return the matching invitation if found
     */
    Optional<Invitation> findByToken(String token);

    /**
     * Finds all invitations sent to a specific user.
     *
     * @param userInvitedId the UserId of the invited person
     * @return a list of all invitations for the user
     */
    List<Invitation> findByUserInvitedId(UserId userInvitedId);

    /**
     * Finds all invitations for a user filtered by status.
     *
     * @param userInvitedId the UserId of the invited person
     * @param status        the invitation status to filter by
     * @return a list of invitations matching the given status
     */
    List<Invitation> findByUserInvitedIdAndStatus(UserId userInvitedId, InvitationStatus status);

    /**
     * Finds all pending and non-expired invitations for a user.
     *
     * @param userInvitedId the UserId of the invited person
     * @param status        the invitation status (typically PENDING)
     * @param now           the current date-time used as the expiry cutoff
     * @return a list of valid pending invitations
     */
    List<Invitation> findByUserInvitedIdAndStatusAndExpiresAtAfter(UserId userInvitedId, InvitationStatus status, LocalDateTime now);

    /**
     * Checks whether a pending invitation already exists for a user in a family group.
     *
     * @param invitedForFamily the family group identifier
     * @param userInvitedId    the UserId of the invited person
     * @param status           the invitation status to check
     * @return true if a matching invitation exists
     */
    boolean existsByInvitedForFamilyAndUserInvitedIdAndStatus(Long invitedForFamily, UserId userInvitedId, InvitationStatus status);

    /**
     * Finds all invitations for a specific family group filtered by status.
     *
     * @param invitedForFamily the family group identifier
     * @param status           the invitation status to filter by
     * @return a list of matching invitations
     */
    List<Invitation> findByInvitedForFamilyAndStatus(Long invitedForFamily, InvitationStatus status);

    /**
     * Finds all invitations for a specific user in a family group filtered by status.
     *
     * @param invitedForFamily the family group identifier
     * @param userInvitedId    the UserId of the invited person
     * @param status           the invitation status to filter by
     * @return a list of matching invitations
     */
    List<Invitation> findByInvitedForFamilyAndUserInvitedIdAndStatus(Long invitedForFamily, UserId userInvitedId, InvitationStatus status);

    /**
     * Checks whether a non-expired pending invitation exists for a user in a family group.
     *
     * @param invitedForFamily the family group identifier
     * @param userInvitedId    the UserId of the invited person
     * @param status           the invitation status to check
     * @param now              the current date-time used as the expiry cutoff
     * @return true if a matching non-expired invitation exists
     */
    boolean existsByInvitedForFamilyAndUserInvitedIdAndStatusAndExpiresAtAfter(
            Long invitedForFamily, UserId userInvitedId, InvitationStatus status, LocalDateTime now);
}

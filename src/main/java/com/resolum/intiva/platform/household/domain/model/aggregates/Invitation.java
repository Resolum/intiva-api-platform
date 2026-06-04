package com.resolum.intiva.platform.household.domain.model.aggregates;

import com.resolum.intiva.platform.household.domain.model.events.FamilyInvitationSentEvent;
import com.resolum.intiva.platform.household.domain.model.events.InvitationAcceptedEvent;
import com.resolum.intiva.platform.household.domain.model.valueobjects.InvitationStatus;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Aggregate root that represents an invitation to join a family group.
 *
 * <p>Invitations are sent by a family group admin and can be accepted or rejected by the invited user.
 * An invitation becomes invalid once it has been responded to or its expiry date has passed.</p>
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "invitations")
public class Invitation extends AuditableAbstractAggregate<Invitation> {

    /**
     * Unique token that identifies this invitation (used for link/QR sharing).
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * Current status of the invitation.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    /**
     * Date and time when the invitation was sent.
     */
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    /**
     * Date and time when the invitation expires.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Date and time when the invited user responded. Null if not yet responded.
     */
    @Column(name = "responded_at", nullable = true)
    private LocalDateTime respondedAt;

    /**
     * The user who sent the invitation.
     */
    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "invited_by", nullable = false))
    private UserId invitedBy;

    /**
     * Identifier of the family group this invitation is for.
     */
    @Column(name = "invited_for_family", nullable = false)
    private Long invitedForFamily;

    /**
     * The user who is being invited.
     */
    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "user_invited_id", nullable = false))
    private UserId userInvitedId;

    /**
     * Creates a new invitation with a generated token and PENDING status.
     *
     * @param expiresAt        the date and time when the invitation expires
     * @param invitedBy        the UserId of the person sending the invitation
     * @param invitedForFamily the ID of the family group the invitation is for
     * @param userInvitedId    the UserId of the person being invited
     */
    public Invitation(LocalDateTime expiresAt, UserId invitedBy, Long invitedForFamily, UserId userInvitedId) {
        this.token = UUID.randomUUID().toString();
        this.status = InvitationStatus.PENDING;
        this.sentAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.invitedBy = invitedBy;
        this.invitedForFamily = invitedForFamily;
        this.userInvitedId = userInvitedId;
        registerEvent(new FamilyInvitationSentEvent(this, invitedForFamily, userInvitedId.getValue(), invitedBy.getValue()));
    }

    /**
     * Accepts the invitation, adds the user to the family group, and registers
     * an {@link InvitationAcceptedEvent}.
     *
     * @throws IllegalStateException if the invitation has already been responded to or has expired
     */
    public void accepts() {
        if (!isPending()) {
            throw new IllegalStateException("Invitation has already been responded");
        }
        if (isExpired()) {
            throw new IllegalStateException("Invitation has expired");
        }
        this.status = InvitationStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
        registerEvent(new InvitationAcceptedEvent(this, this.getId(), this.invitedForFamily, this.userInvitedId.getValue()));
    }

    /**
     * Rejects the invitation.
     *
     * @throws IllegalStateException if the invitation has already been responded to or has expired
     */
    public void rejects() {
        if (!isPending()) {
            throw new IllegalStateException("Invitation has already been responded");
        }
        if (isExpired()) {
            throw new IllegalStateException("Invitation has expired");
        }
        this.status = InvitationStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }

    /**
     * Returns whether this invitation has passed its expiry date.
     *
     * @return true if the current time is after the expiry date
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Returns whether this invitation is still pending a response.
     *
     * @return true if the status is PENDING
     */
    public boolean isPending() {
        return this.status == InvitationStatus.PENDING;
    }
}

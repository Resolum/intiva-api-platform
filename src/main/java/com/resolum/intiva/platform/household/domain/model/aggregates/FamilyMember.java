package com.resolum.intiva.platform.household.domain.model.aggregates;

import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyMemberStatus;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyRole;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Aggregate root that represents a member within a family group.
 *
 * <p>A family member links a user to a family group with a specific role (ADMIN or MEMBER).
 * Members are created when the owner creates the group or when an invitation is accepted.</p>
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "family_members")
public class FamilyMember extends AuditableAbstractAggregate<FamilyMember> {

    /**
     * Role assigned to this member within the family group.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyRole role;

    /**
     * Current status of this member's participation in the family group.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyMemberStatus status;

    /**
     * Date and time when the user joined the family group.
     */
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    /**
     * Identifier of the family group this member belongs to.
     */
    @Column(name = "family_id", nullable = false)
    private Long familyId;

    /**
     * Identifier of the user this member record represents.
     */
    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "user_id", nullable = false))
    private UserId userId;

    /**
     * Creates a new family member for the given family group and user.
     *
     * @param familyId the ID of the family group
     * @param userId   the UserId value object of the user joining the group
     * @param role     the role assigned to this member
     */
    public FamilyMember(Long familyId, UserId userId, FamilyRole role) {
        this.familyId = familyId;
        this.userId = userId;
        this.role = role;
        this.status = FamilyMemberStatus.ACTIVE;
        this.joinedAt = Instant.now();
    }

    /**
     * Assigns a new role to this member.
     *
     * @param role the new role to assign
     * @throws IllegalStateException if the member has been expelled
     */
    public void asignRole(FamilyRole role) {
        if (this.status != FamilyMemberStatus.ACTIVE) {
            throw new IllegalStateException("Cannot assign role to expelled member");
        }
        this.role = role;
    }

    /**
     * Expels this member from the family group.
     */
    public void expel() {
        this.status = FamilyMemberStatus.EXPELLED;
    }
}

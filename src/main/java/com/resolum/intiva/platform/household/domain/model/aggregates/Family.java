package com.resolum.intiva.platform.household.domain.model.aggregates;

import com.resolum.intiva.platform.household.domain.model.commands.CreateFamilyCommand;
import com.resolum.intiva.platform.household.domain.model.events.FamilyCreatedEvent;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyResourcesUsage;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyStatus;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Aggregate root that represents a family group in the household bounded context.
 *
 * <p>A family group is owned by one user (the ADMIN) and can have multiple members.
 * Members join by accepting an invitation sent by the owner.</p>
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "families")
public class Family extends AuditableAbstractAggregate<Family> {

    /**
     * Human-readable name of the family group.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Optional description of the family group.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Current lifecycle status of the family group.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FamilyStatus status;

    /**
     * Identifier of the user who owns and administers the family group.
     */
    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "owner_id", nullable = false))
    private UserId ownerId;

    /**
     * Counters tracking resource usage within this family group.
     */
    @Embedded
    private FamilyResourcesUsage resourcesUsage;

    /**
     * Creates a new family group from the provided command.
     * Registers a {@link FamilyCreatedEvent} to be published after persistence.
     *
     * @param command the command containing the family group details
     */
    public Family(CreateFamilyCommand command) {
        this.name = command.name();
        this.description = command.description();
        this.status = FamilyStatus.ACTIVE;
        this.ownerId = command.ownerId();
        this.resourcesUsage = new FamilyResourcesUsage();
        registerEvent(new FamilyCreatedEvent(this, this.ownerId.getValue(), this.name));
    }

    /**
     * Dissolves the family group by setting its status to DISOLVED.
     */
    public void disolve() {
        this.status = FamilyStatus.DISOLVED;
    }

    /**
     * Returns whether the family group can invite new members.
     *
     * @return true if the group is ACTIVE
     */
    public boolean canInviteMembers() {
        return this.status == FamilyStatus.ACTIVE;
    }

    /**
     * Returns whether the family group can set spending limits.
     *
     * @return true if the group is ACTIVE
     */
    public boolean canSetSpendingLimits() {
        return this.status == FamilyStatus.ACTIVE;
    }

    /**
     * Returns whether the family group can set saving goals.
     *
     * @return true if the group is ACTIVE
     */
    public boolean canSetSavingsGoals() {
        return this.status == FamilyStatus.ACTIVE;
    }
}

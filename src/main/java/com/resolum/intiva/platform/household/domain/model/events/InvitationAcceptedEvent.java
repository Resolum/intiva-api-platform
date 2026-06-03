package com.resolum.intiva.platform.household.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain event published when an invitation to a family group is accepted.
 * Consumed by event handlers that need to react to a user joining a family group.
 */
@Getter
public class InvitationAcceptedEvent extends ApplicationEvent {

    /**
     * The identifier of the accepted invitation.
     */
    private final Long invitationId;

    /**
     * The identifier of the family group the user joined.
     */
    private final Long familyId;

    /**
     * The user identifier of the person who accepted the invitation.
     */
    private final Long userId;

    /**
     * Creates a new InvitationAcceptedEvent.
     *
     * @param source       the aggregate that published the event
     * @param invitationId the ID of the accepted invitation
     * @param familyId     the ID of the family group
     * @param userId       the numeric user ID of the person who accepted
     */
    public InvitationAcceptedEvent(Object source, Long invitationId, Long familyId, Long userId) {
        super(source);
        this.invitationId = invitationId;
        this.familyId = familyId;
        this.userId = userId;
    }
}

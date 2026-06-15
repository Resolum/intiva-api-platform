package com.resolum.intiva.platform.household.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain event published when an invitation to a family group is rejected.
 */
@Getter
public class InvitationRejectedEvent extends ApplicationEvent {

    private final Long invitationId;
    private final Long familyId;
    private final Long inviterUserId;
    private final Long rejectorId;
    private final String rejectorName;

    public InvitationRejectedEvent(
            Object source,
            Long invitationId,
            Long familyId,
            Long inviterUserId,
            Long rejectorId,
            String rejectorName
    ) {
        super(source);
        this.invitationId = invitationId;
        this.familyId = familyId;
        this.inviterUserId = inviterUserId;
        this.rejectorId = rejectorId;
        this.rejectorName = rejectorName;
    }
}

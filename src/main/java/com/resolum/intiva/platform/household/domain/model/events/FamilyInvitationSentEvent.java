package com.resolum.intiva.platform.household.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FamilyInvitationSentEvent extends ApplicationEvent {

    private final Long familyId;
    private final Long invitedUserId;
    private final Long invitedByUserId;

    public FamilyInvitationSentEvent(Object source, Long familyId, Long invitedUserId, Long invitedByUserId) {
        super(source);
        this.familyId = familyId;
        this.invitedUserId = invitedUserId;
        this.invitedByUserId = invitedByUserId;
    }
}

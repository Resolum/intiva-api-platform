package com.resolum.intiva.platform.household.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain event published when a new family group is created.
 * Consumed by event handlers that need to react to family group creation.
 */
@Getter
public class FamilyCreatedEvent extends ApplicationEvent {

    /**
     * The identifier of the user who owns the newly created family group.
     */
    private final Long ownerId;

    /**
     * The name of the newly created family group.
     */
    private final String familyName;

    /**
     * Creates a new FamilyCreatedEvent.
     *
     * @param source     the aggregate that published the event
     * @param ownerId    the numeric user ID of the owner
     * @param familyName the name of the created family group
     */
    public FamilyCreatedEvent(Object source, Long ownerId, String familyName) {
        super(source);
        this.ownerId = ownerId;
        this.familyName = familyName;
    }
}

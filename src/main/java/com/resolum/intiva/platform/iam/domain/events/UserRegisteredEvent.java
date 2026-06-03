package com.resolum.intiva.platform.iam.domain.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event triggered when a new user is registered.
 */
@Getter
public class UserRegisteredEvent extends ApplicationEvent {

    /** The ID of the user that was registered. */
    private final Long userId;

    /**
     * Constructor for UserRegisteredEvent.
     * @param source the source of the event
     * @param userId the ID of the user that was registered
     */
    public UserRegisteredEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}

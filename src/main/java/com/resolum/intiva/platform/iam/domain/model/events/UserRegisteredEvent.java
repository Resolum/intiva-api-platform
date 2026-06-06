package com.resolum.intiva.platform.iam.domain.model.events;

import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import lombok.Getter;

/**
 * Event triggered when a new user is registered.
 */
@Getter
public class UserRegisteredEvent {

    /**
     * Aggregate instance that raised the event.
     */
    private final User user;

    /**
     * Constructor for UserRegisteredEvent.
     *
     * @param user the user aggregate that has just been registered
     */
    public UserRegisteredEvent(User user) {
        this.user = user;
    }

    /**
     * Returns the persisted user identifier at the moment the event is consumed.
     *
     * @return persisted user id
     */
    public Long getUserId() {
        return user.getId();
    }
}

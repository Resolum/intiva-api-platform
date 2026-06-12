package com.resolum.intiva.platform.profiles.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the creation of a new user profile fails.
 *
 * <p>This is typically used in the {@link com.resolum.intiva.platform.profiles.application.internal.eventhandlers.UserRegisteredEventHandler}
 * or creation flows when the profile cannot be persisted due to validation or infrastructure errors.</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProfileCreationFailedException extends RuntimeException {
    public ProfileCreationFailedException(String message) {
        super(message);
    }
}

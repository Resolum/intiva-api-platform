package com.resolum.intiva.platform.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Record holding the unique identifier for a user in the system.
 *
 * @param value the unique identifier value for the user, must not be null or blank
 */
@Embeddable
public record UserId(String value) {

    // Constructor to validate the user ID
    public UserId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }

    /**
     * Returns the unique identifier value for the user.
     * @return the user ID value
     */
    public String getValue() {
        return value;
    }
}

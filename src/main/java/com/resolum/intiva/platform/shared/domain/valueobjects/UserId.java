package com.resolum.intiva.platform.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Record holding the unique identifier for a user in the system.
 *
 * @param userId the unique identifier value for the user, must not be null or blank
 */
@Embeddable
public record UserId(Long userId) {

    // Constructor to validate the user ID
    public UserId {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null or blank");
        }
    }

    /**
     * Returns the unique identifier value for the user.
     * @return the user ID value
     */
    public Long getValue() {
        return userId;
    }
}

package com.resolum.intiva.platform.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing a password hash.
 *
 * @param value the hashed password value
 */
@Embeddable
public record PasswordHash(String value) {

    // Constructor to validate the password hash
    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }
    }

    /**
     * Updates the password hash with a new value.
     *
     * @param newHash the new password hash value
     * @return a new PasswordHash instance with the updated value
     */
    public PasswordHash update(String newHash) {
        return new PasswordHash(newHash);
    }
}

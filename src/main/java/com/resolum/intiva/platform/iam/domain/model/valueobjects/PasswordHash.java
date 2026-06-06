package com.resolum.intiva.platform.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing a password hash.
 *
 * @param passwordHash the hashed password value
 */
@Embeddable
public record PasswordHash(String passwordHash) {

    // Constructor to validate the password hash
    public PasswordHash {
        if (passwordHash == null || passwordHash.isBlank()) {
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

    /**
     * Retrieves the password hash value.
     * @return the password hash value
     */
    public String getValue() {
        return passwordHash;
    }
}

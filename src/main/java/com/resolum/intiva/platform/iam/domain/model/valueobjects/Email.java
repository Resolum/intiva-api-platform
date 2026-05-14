package com.resolum.intiva.platform.iam.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing an email address.
 * @param value the email address string
 */
@Embeddable
public record Email(String value) {

    // Constructor to validate the email format
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (!isEmailFormatValid(value)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validates the email format using a simple regex pattern.
     * @param email the email string to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isEmailFormatValid(String email) {
        // Simple regex for email validation
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
     * Returns the email value.
     * @return the email string
     */
    public String getValue() {
        return value;
    }
}

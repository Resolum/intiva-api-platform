package com.resolum.intiva.platform.categories.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing the institution associated with a payment method.
 */
@Embeddable
public record Institution(String institutionName) {

    /**
     * Validates the institution name.
     *
     * @throws IllegalArgumentException if the institution name is blank or exceeds 50 characters.
     */
    public Institution {
        if (institutionName.isBlank())
            throw new IllegalArgumentException("The institution name cannot be blank");
        if (institutionName.length() > 50)
            throw new IllegalArgumentException("The institution name cannot be longer than 50 characters");
    }

    /**
     * Retrieves the name of the institution.
     *
     * @return the institution name
     */
    public String getInstitutionName() {
        return institutionName;
    }
}

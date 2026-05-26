package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing a category description.
 * @param description the description of the category
 */
@Embeddable
public record CategoryDescription(String description) {

    /**
     * Constructs a CategoryDescription with the given description.
     * Validates that the description does not exceed 15 characters.
     * @param description the description of the category
     * @throws IllegalArgumentException if the description exceeds 15 characters
     */
    public CategoryDescription {
        if (description.length() > 20)
            throw new IllegalArgumentException("Maximum 20 characters allowed for category description");
    }

    /**
     * Returns the description of the category.
     * @return the category description
     */
    public String getDescription() {
        return description;
    }
}

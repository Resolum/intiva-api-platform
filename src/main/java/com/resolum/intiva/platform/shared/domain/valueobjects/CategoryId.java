package com.resolum.intiva.platform.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * CategoryId is a value object that represents the identifier of a category in the system. It encapsulates a Long value that serves as the unique identifier for a category.
 * This class is designed to be immutable and provides validation to ensure that the categoryId is not null and is a positive number. It is annotated with @Embeddable, indicating that it can be embedded in other entities or value objects as part of the persistence layer.
 * @param categoryId the unique identifier for a category, represented as a Long value. This field is mandatory and must be a positive number.
 */
@Embeddable
public record CategoryId(Long categoryId) {

    // Constructor with validation
    public CategoryId {
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
        if (categoryId <= 0) {
            throw new IllegalArgumentException("CategoryId must be a positive number");
        }
    }

    /**
     * Returns the value of the categoryId.
     * @return the categoryId value as a Long.
     */
    public Long getValue() {
        return categoryId;
    }
}

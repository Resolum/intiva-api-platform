package com.resolum.intiva.platform.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value Object representing an icon.
 * This class is immutable and validates that the icon string is not null or blank.
 */
@Embeddable
public record Icon(String icon) {

    /**
     * Constructs an Icon value object.
     *
     * @param icon the string representation of the icon, must not be null or blank
     * @throws IllegalArgumentException if the icon is null or blank
     */
    public Icon(String icon) {
        if (icon == null || icon.isBlank()) {
            throw new IllegalArgumentException("This icon cannot be null or blank");
        }
        this.icon = icon;
    }

    /**
     * Returns the string representation of the icon.
     * @return A string representing the icon.
     */
    public String getIcon() {
        return icon;
    }
}

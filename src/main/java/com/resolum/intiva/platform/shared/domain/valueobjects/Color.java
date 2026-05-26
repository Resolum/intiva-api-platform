package com.resolum.intiva.platform.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record Color(String color) {
    public Color {
        if (!color.matches("^#[0-9A-Fa-f]{6}$"))
            throw new IllegalArgumentException("The hex color code must be in the format #RRGGBB");
    }

    public String getColor() {
        return color;
    }
}

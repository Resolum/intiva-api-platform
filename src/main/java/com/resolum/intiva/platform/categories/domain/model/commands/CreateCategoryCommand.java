package com.resolum.intiva.platform.categories.domain.model.commands;

import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryType;

public record CreateCategoryCommand(
    String name,
    String ownerType,
    Long ownerId,
    String description,
    String color,
    String icon,
    CategoryType type
) {
    public CreateCategoryCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("El color de la categoría es obligatorio.");
        }
        if (ownerType == null || ownerType.isBlank()) {
            throw new IllegalArgumentException("El tipo de propietario (ownerType) es obligatorio.");
        }
    }
}
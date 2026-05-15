package com.resolum.intiva.platform.categories.domain.model.commands;

import java.util.UUID;

public record CreateCategoryCommand(
    String name,
    String color,
    String ownerType,
    Long userId,
    Long groupId
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
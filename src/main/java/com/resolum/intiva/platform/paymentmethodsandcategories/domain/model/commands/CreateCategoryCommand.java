package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands;

public record CreateCategoryCommand(
    String name,
    String ownerType,
    Long userId,
    Long groupId,
    String description,
    String color,
    String icon
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
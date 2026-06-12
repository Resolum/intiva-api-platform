package com.resolum.intiva.platform.categories.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resource representing a category.
 * @param id the id of the category
 * @param name the name of the category
 * @param ownerType the owner type of the category (user or group)
 * @param ownerId the id of the user who owns the category
 * @param isActive whether the category is active
 * @param description the description of the category
 * @param color the color of the category
 * @param icon the icon of the category
 */
@Schema(
        name = "CategoryResource",
        description = "Resource representing a category."
)
public record CategoryResource(
        @Schema(description = "The unique identifier of the category.", example = "1")
        Long id,
        @Schema(description = "The name of the category.", example = "Groceries")
        String name,
        @Schema(description = "The owner type of the category (user or group).", example = "individual")
        String ownerType,
        @Schema(description = "The id of the user who owns the category.", example = "123")
        Long ownerId,
        @Schema(description = "Whether the category is active.", example = "true")
        Boolean isActive,
        @Schema(description = "The description of the category.", example = "Expenses related to groceries.")
        String description,
        @Schema(description = "The color of the category.", example = "#FF5733")
        String color,
        @Schema(description = "The icon of the category.", example = "shopping_cart")
        String icon
) {}
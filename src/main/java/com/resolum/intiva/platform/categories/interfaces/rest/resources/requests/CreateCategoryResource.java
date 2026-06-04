package com.resolum.intiva.platform.categories.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "CreateCategoryResource",
        description = "Resource representing the data required to create a new category."
)
public record CreateCategoryResource(
        @Schema(description = "The name of the category", example = "Work")
        String name,
        @Schema(description = "The type of the owner of the category", example = "individual")
        String ownerType,
        @Schema(description = "The ID of the owner of the category", example = "123")
        Long ownerId,
        @Schema(description = "The description of the category", example = "Tasks related to work")
        String description,
        @Schema(description = "The color of the category in hex format", example = "#FF5733")
        String color,
        @Schema(description = "The icon representing the category", example = "briefcase")
        String icon
) {}
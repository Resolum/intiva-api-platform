package com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.resources.responses;

/**
 * Resource representing a category.
 * @param id the id of the category
 * @param name the name of the category
 * @param ownerType the owner type of the category (user or group)
 * @param userId the id of the user who owns the category
 * @param groupId the id of the group who owns the category
 * @param isActive whether the category is active
 * @param description the description of the category
 * @param color the color of the category
 * @param icon the icon of the category
 */
public record CategoryResource(
        Long id,
        String name,
        String ownerType,
        Long userId,
        Long groupId,
        Boolean isActive,
        String description,
        String color,
        String icon
) {}
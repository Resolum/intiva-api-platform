package com.resolum.intiva.platform.finances.interfaces.rest.resources.responses;

/**
 * Record representing the design details of a category, including its color and icon.
 * This resource is used to encapsulate the visual design elements of a category for presentation in REST responses.
 *
 * @param categoryColor the color associated with the category
 * @param categoryIcon the icon associated with the category
 */
public record CategoryDesignResource(
        String categoryColor,
        String categoryIcon
) {
}

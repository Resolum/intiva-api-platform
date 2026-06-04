package com.resolum.intiva.platform.categories.domain.model.queries;

/**
 * Query object for retrieving the color and icon of a category by its identifier. This query is used to request the necessary information to display the category's visual representation in the user interface.
 *
 * @param categoryId The unique identifier of the category for which the color and icon are being requested. This field is mandatory and must be a valid Long value corresponding to an existing category in the system.
 */
public record GetCategoryColorAndIconByIdQuery(Long categoryId) {
}

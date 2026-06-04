package com.resolum.intiva.platform.finances.domain.model.queries;

/**
 * Query used to retrieve spending limits that target a category.
 *
 * @param categoryId category identifier
 */
public record GetSpendingLimitsByCategoryIdQuery(Long categoryId) {
}

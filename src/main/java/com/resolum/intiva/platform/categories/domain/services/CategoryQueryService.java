package com.resolum.intiva.platform.categories.domain.services;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.domain.model.queries.GetAllCategoriesByOwnerTypeAndOwnerId;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryByIdQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryColorAndIconByIdQuery;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.Optional;

public interface CategoryQueryService {

    /**
     * Handle the GetCategoryByIdQuery to retrieve a category by its id.
     * @param query the query containing the category id
     * @return an Optional containing the category if found, or empty if not found
     */
    Optional<Category> handle(GetCategoryByIdQuery query);

    /**
     * Handle the GetAllCategoriesByOwnerTypeAndOwnerId query to retrieve all categories for a given owner type and owner id.
     *
     * @param query the query containing the owner type and owner id
     * @return a list of categories associated with the specified owner type and owner id
     */
    List<Category> handle(GetAllCategoriesByOwnerTypeAndOwnerId query);

    /**
     * Check if a category exists by id
     * @param categoryId the category id
     * @return true if the category exists, false otherwise
     */
    boolean existsCategoryById(Long categoryId);

    /**
     * Handle the GetCategoryColorAndIconByIdQuery to retrieve the color and icon of a category by its id.
     * @param query the query containing the category id
     * @return an Optional containing an ImmutablePair of color and icon if found, or empty if not found
     */
    Optional<ImmutablePair<String, String>> getCategoryColorAndIconById(GetCategoryColorAndIconByIdQuery query);
}
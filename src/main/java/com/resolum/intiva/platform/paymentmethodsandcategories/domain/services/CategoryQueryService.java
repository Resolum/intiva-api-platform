package com.resolum.intiva.platform.paymentmethodsandcategories.domain.services;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetAllCategoriesByUserIdQuery;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetCategoryByIdQuery;

import java.util.List;
import java.util.Optional;

public interface CategoryQueryService {
    Optional<Category> handle(GetCategoryByIdQuery query);
    List<Category> handle(GetAllCategoriesByUserIdQuery query);

    /**
     * Check if a category exists by id
     * @param categoryId the category id
     * @return true if the category exists, false otherwise
     */
    boolean existsCategoryById(Long categoryId);
}
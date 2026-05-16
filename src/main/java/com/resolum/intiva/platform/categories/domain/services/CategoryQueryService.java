package com.resolum.intiva.platform.categories.domain.services;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.domain.model.queries.GetAllCategoriesByUserIdQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryByIdQuery;

import java.util.List;
import java.util.Optional;

public interface CategoryQueryService {
    Optional<Category> handle(GetCategoryByIdQuery query);
    List<Category> handle(GetAllCategoriesByUserIdQuery query);
}
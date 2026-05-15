package com.resolum.intiva.platform.categories.application.internal.queryhandlers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.domain.model.queries.GetAllCategoriesByUserIdQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryByIdQuery;
import com.resolum.intiva.platform.categories.domain.services.CategoryQueryService;
import com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public CategoryQueryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Category> handle(GetCategoryByIdQuery query) {
        return categoryRepository.findById(query.id());
    }

    @Override
    public List<Category> handle(GetAllCategoriesByUserIdQuery query) {
        return categoryRepository.findAllByUserId(query.userId());
    }
}
package com.resolum.intiva.platform.paymentmethodsandcategories.application.internal.queryhandlers;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetAllCategoriesByUserIdQuery;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetCategoryByIdQuery;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.services.CategoryQueryService;
import com.resolum.intiva.platform.paymentmethodsandcategories.infraestructure.persistence.jpa.repositories.CategoryRepository;
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

    @Override
    public boolean existsCategoryById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}
package com.resolum.intiva.platform.paymentmethodsandcategories.application.internal.queryhandlers;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetAllCategoriesByUserIdQuery;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetCategoryByIdQuery;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetCategoryColorAndIconByIdQuery;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.services.CategoryQueryService;
import com.resolum.intiva.platform.paymentmethodsandcategories.infraestructure.persistence.jpa.repositories.CategoryRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CategoryQueryService interface that provides methods for handling queries related to categories. This service interacts with the CategoryRepository to retrieve category data from the database.
 */
@Service
public class CategoryQueryServiceImpl implements CategoryQueryService {

    /**
     * The CategoryRepository is used to perform database operations related to categories. It provides methods for finding categories by ID, finding all categories by user ID, checking for the existence of a category by ID, and retrieving category name and description.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Constructs a new CategoryQueryServiceImpl with the specified CategoryRepository.
     *
     * @param categoryRepository the repository used for accessing category data from the database
     */
    public CategoryQueryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Handles the GetCategoryByIdQuery by retrieving the category with the specified ID from the repository.
     *
     * @param query the query containing the ID of the category to retrieve
     * @return an Optional containing the Category if found, or an empty Optional if not found
     */
    @Override
    public Optional<Category> handle(GetCategoryByIdQuery query) {
        return categoryRepository.findById(query.id());
    }

    /**
     * Handles the GetAllCategoriesByUserIdQuery by retrieving all categories associated with the specified user ID from the repository.
     *
     * @param query the query containing the user ID for which to retrieve categories
     * @return a list of Category objects associated with the specified user ID
     */
    @Override
    public List<Category> handle(GetAllCategoriesByUserIdQuery query) {
        return categoryRepository.findAllByUserId(query.userId());
    }

    /**
     * Checks if a category with the specified ID exists in the repository.
     *
     * @param categoryId the ID of the category to check for existence
     * @return true if a category with the specified ID exists, false otherwise
     */
    @Override
    public boolean existsCategoryById(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    /**
     * Retrieves the category name and description for a given category ID.
     *
     * @param query the query containing the category ID for which to retrieve the name and description
     * @return an Optional containing an ImmutablePair with the category name and description if found, or an empty Optional if not found
     */
    @Override
    public Optional<ImmutablePair<String, String>> getCategoryColorAndIconById(GetCategoryColorAndIconByIdQuery query) {
        return categoryRepository.findById(query.categoryId()).map(category -> new ImmutablePair<>(category.getColor().getColor(), category.getIcon().getIcon()));
    }
}
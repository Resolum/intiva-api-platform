package com.resolum.intiva.platform.categories.application.internal.queryhandlers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.domain.model.queries.GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryByIdQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryColorAndIconByIdQuery;
import com.resolum.intiva.platform.categories.domain.services.CategoryQueryService;
import com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CategoryQueryService interface that provides methods for handling queries related to categories. This service interacts with the CategoryRepository to retrieve category data from the database.
 */
@Slf4j
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
    @Transactional(readOnly = true)
    public Optional<Category> handle(GetCategoryByIdQuery query) {
        log.info(
                "{} - Fetching category with id {}",
                query.getClass().getSimpleName(),
                query.id()
        );
        return categoryRepository.findById(query.id());
    }

    /**
     * Handle the GetAllCategoriesByOwnerTypeAndOwnerId query to retrieve all categories for a given owner type and owner id.
     *
     * @param query the query containing the owner type and owner id
     * @return a list of categories associated with the specified owner type and owner id
     */
    @Override
    @Transactional(readOnly = true)
    public List<Category> handle(GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery query) {
        log.info(
                "{} - Fetching all categories for owner type {} and owner id {}",
                query.getClass().getSimpleName(),
                query.ownerType(),
                query.ownerId()
        );
        return categoryRepository.findAllByOwnerTypeAndOwnerIdAndType(query.ownerType().toUpperCase(), query.ownerId(), query.type());
    }

    /**
     * Checks if a category with the specified ID exists in the repository.
     *
     * @param categoryId the ID of the category to check for existence
     * @return true if a category with the specified ID exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsCategoryById(Long categoryId) {
        log.info(
                "{} - Checking existence of category with id {}",
                "ExistsCategoryById",
                categoryId
        );
        return categoryRepository.existsById(categoryId);
    }

    /**
     * Retrieves the category name and description for a given category ID.
     *
     * @param query the query containing the category ID for which to retrieve the name and description
     * @return an Optional containing an ImmutablePair with the category name and description if found, or an empty Optional if not found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ImmutablePair<String, String>> getCategoryColorAndIconById(GetCategoryColorAndIconByIdQuery query) {
        log.info(
                "{} - Fetching category color and icon for category id {}",
                query.getClass().getSimpleName(),
                query.categoryId()
        );
        return categoryRepository
                .findById(query.categoryId())
                .map(category -> new ImmutablePair<>(category.getColor().getColor(), category.getIcon().getIcon()));
    }
}
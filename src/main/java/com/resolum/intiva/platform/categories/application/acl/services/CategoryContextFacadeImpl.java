package com.resolum.intiva.platform.categories.application.acl.services;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateDefaultCategoryCommand;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryByIdQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryColorAndIconByIdQuery;
import com.resolum.intiva.platform.categories.domain.services.CategoryCommandService;
import com.resolum.intiva.platform.categories.domain.services.CategoryQueryService;
import com.resolum.intiva.platform.categories.interfaces.acl.CategoriesContextFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Category Context Facade Implementation.
 * This class serves as the implementation of the CategoriesContextFacade interface, providing methods to interact with category-related operations. It uses the CategoryQueryService and CategoryCommandService to perform queries and commands related to categories.
 */
@Slf4j
@Service
public class CategoryContextFacadeImpl implements CategoriesContextFacade {

    // Category Query Service
    private final CategoryQueryService categoryQueryService;

    // Category Command Service
    private final CategoryCommandService categoryCommandService;

    // Default constructor
    public CategoryContextFacadeImpl(CategoryQueryService categoryQueryService, CategoryCommandService categoryCommandService) {
        this.categoryQueryService = categoryQueryService;
        this.categoryCommandService = categoryCommandService;
    }

    /**
     * Check if a category exists by id
     *
     * @param categoryId the category id
     * @return true if a category exists, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsCategoryById(Long categoryId) {
        log.info(
                "ACL - Checking if category exists by id {}",
                categoryId
        );
        return categoryQueryService.existsCategoryById(categoryId);
    }

    /**
     * Create a new category
     *
     * @param userId the user id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDefaultCategory(Long userId) {
        log.info(
                "ACL - Creating default category for user with id {}",
                userId
        );
        var createDefaultCategoryCommand = new CreateDefaultCategoryCommand(
                userId
        );
        categoryCommandService.handle(createDefaultCategoryCommand);
    }

    /**
     * Get a category color and icon by id
     *
     * @param categoryId the category id
     * @return the category color and icon
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImmutablePair<String, String> getCategoryColorAndIconById(Long categoryId) {
        log.info(
                "ACL - Getting category color and icon by id {}",
                categoryId
        );

        var getCategoryColorAndIconQuery = new GetCategoryColorAndIconByIdQuery(
                categoryId
        );
        return categoryQueryService.getCategoryColorAndIconById(getCategoryColorAndIconQuery).orElse(null);
    }

    /**
     * Get a category name by id
     *
     * @param categoryId the category id
     * @return the category name
     */
    @Override
    @Transactional(readOnly = true)
    public String getCategoryNameById(Long categoryId) {
        log.info(
                "ACL - Getting category name by id {}",
                categoryId
        );
        var getCategoryByIdQuery = new GetCategoryByIdQuery(
                categoryId
        );
        return categoryQueryService.handle(getCategoryByIdQuery).map(Category::getName).orElse(null);
    }
}
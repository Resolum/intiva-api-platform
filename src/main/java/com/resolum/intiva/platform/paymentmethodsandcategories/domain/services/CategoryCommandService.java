package com.resolum.intiva.platform.paymentmethodsandcategories.domain.services;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateDefaultCategoryCommand;

import java.util.Optional;

/**
 * Service interface for handling category-related commands.
 */
public interface CategoryCommandService {

    /**
     * Handles the creation of a new category.
     * @param command the command containing the category details
     * @return an Optional containing the created Category if successful, or empty if the operation failed (e.g., due to validation errors).
     */
    Optional<Category> handle(CreateCategoryCommand command);

    /**
     * Creates a default category for a user.
     * @param command the command containing the category details
     */
    void handle(CreateDefaultCategoryCommand command);
}
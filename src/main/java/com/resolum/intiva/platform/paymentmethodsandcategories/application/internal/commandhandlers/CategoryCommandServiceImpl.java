package com.resolum.intiva.platform.paymentmethodsandcategories.application.internal.commandhandlers;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateDefaultCategoryCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.services.CategoryCommandService;
import  com.resolum.intiva.platform.paymentmethodsandcategories.infraestructure.persistence.jpa.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *  * Implementation of the CategoryCommandService interface, responsible for handling category-related commands.
 */
@Service
public class CategoryCommandServiceImpl implements CategoryCommandService {

    /**
     * Repository for accessing category data from the database.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Constructor for CategoryCommandServiceImpl.
     *
     * @param categoryRepository the repository for accessing category data
     */
    public CategoryCommandServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Handles the creation of a new category.
     * @param command the command containing the category details
     * @return an Optional containing the created Category if successful, or empty if the operation failed (e.g., due to validation errors).
     */
    @Override
    public Optional<Category> handle(CreateCategoryCommand command) {
        var category = new Category(command);
        var savedCategory = categoryRepository.save(category);
        return Optional.of(savedCategory);
    }

    /**
     * Handles the creation of a default category for a user.
     * @param command the command containing the user ID for whom the default category should be created
     */
    @Override
    public void handle(CreateDefaultCategoryCommand command) {
        var categories = Category.createDefault(command.userId());
        categoryRepository.saveAll(categories);
    }
}
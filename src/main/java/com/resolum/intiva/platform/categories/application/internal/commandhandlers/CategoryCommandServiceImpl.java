package com.resolum.intiva.platform.categories.application.internal.commandhandlers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateDefaultCategoryCommand;
import com.resolum.intiva.platform.categories.domain.services.CategoryCommandService;
import  com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 *  * Implementation of the CategoryCommandService interface, responsible for handling category-related commands.
 */
@Slf4j
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
    @Transactional(rollbackFor = Exception.class)
    public Optional<Category> handle(CreateCategoryCommand command) {
        log.info(
                "{} - Creating category with name {} for {} with id {}",
                command.getClass().getSimpleName(),
                command.name(),
                command.ownerType(),
                command.ownerId()
        );
        var category = new Category(command);
        var savedCategory = categoryRepository.save(category);
        return Optional.of(savedCategory);
    }

    /**
     * Handles the creation of a default category for a user.
     * @param command the command containing the user ID for whom the default category should be created
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(CreateDefaultCategoryCommand command) {
        log.info(
                "{} - Creating default category for user with id {}",
                command.getClass().getSimpleName(),
                command.userId()
        );
        var categories = Category.createDefault(command.userId());
        categoryRepository.saveAll(categories);
    }
}
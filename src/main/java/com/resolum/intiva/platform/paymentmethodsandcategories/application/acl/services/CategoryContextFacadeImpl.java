package com.resolum.intiva.platform.paymentmethodsandcategories.application.acl.services;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateDefaultCategoryCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.services.CategoryCommandService;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.services.CategoryQueryService;
import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl.CategoriesContextFacade;
import org.springframework.stereotype.Service;

@Service
public class CategoryContextFacadeImpl implements CategoriesContextFacade {

    private final CategoryQueryService categoryQueryService;
    private final CategoryCommandService categoryCommandService;

    public CategoryContextFacadeImpl(CategoryQueryService categoryQueryService, CategoryCommandService categoryCommandService) {
        this.categoryQueryService = categoryQueryService;
        this.categoryCommandService = categoryCommandService;
    }

    /**
     * Check if a category exists by id
     * @param categoryId the category id
     * @return true if a category exists, false otherwise
     */
    @Override
    public boolean existsCategoryById(Long categoryId) {
        return categoryQueryService.existsCategoryById(categoryId);
    }

    /**
     * Create a new category

     * @param userId the user id
     */
    @Override
    public void createDefaultCategory(Long userId) {
        var createDefaultCategoryCommand = new CreateDefaultCategoryCommand(
                userId
        );
        categoryCommandService.handle(createDefaultCategoryCommand);
    }
}

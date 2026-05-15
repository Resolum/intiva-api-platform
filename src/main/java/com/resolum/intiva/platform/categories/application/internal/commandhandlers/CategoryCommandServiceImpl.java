package com.resolum.intiva.platform.categories.application.internal.commandhandlers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.categories.domain.services.CategoryCommandService;
import  com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryRepository categoryRepository;

    public CategoryCommandServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Category> handle(CreateCategoryCommand command) {
        // Aquí se pueden agregar validaciones de dominio complejas si es necesario
        var category = new Category(command);
        var savedCategory = categoryRepository.save(category);
        return Optional.of(savedCategory);
    }
}
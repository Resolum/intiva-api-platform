package com.resolum.intiva.platform.categories.domain.services;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateCategoryCommand;

import java.util.Optional;

public interface CategoryCommandService {
    Optional<Category> handle(CreateCategoryCommand command);
}
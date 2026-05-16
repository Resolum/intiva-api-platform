package com.resolum.intiva.platform.categories.interfaces.rest.controllers;

import com.resolum.intiva.platform.categories.domain.model.queries.GetAllCategoriesByUserIdQuery;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryByIdQuery;
import com.resolum.intiva.platform.categories.domain.services.CategoryCommandService;
import com.resolum.intiva.platform.categories.domain.services.CategoryQueryService;
import com.resolum.intiva.platform.categories.interfaces.rest.assemblers.CategoryResourceFromEntityAssembler;
import com.resolum.intiva.platform.categories.interfaces.rest.assemblers.CreateCategoryCommandFromResourceAssembler;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.requests.CreateCategoryResource;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.responses.CategoryResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoriesController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;

    public CategoriesController(CategoryCommandService categoryCommandService, CategoryQueryService categoryQueryService) {
        this.categoryCommandService = categoryCommandService;
        this.categoryQueryService = categoryQueryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResource> createCategory(@RequestBody CreateCategoryResource resource) {
        var command = CreateCategoryCommandFromResourceAssembler.toCommandFromResource(resource);
        var category = categoryCommandService.handle(command);

        if (category.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var categoryResource = CategoryResourceFromEntityAssembler.toResourceFromEntity(category.get());
        return new ResponseEntity<>(categoryResource, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResource> getCategoryById(@PathVariable Long id) {
        var query = new GetCategoryByIdQuery(id);
        var category = categoryQueryService.handle(query);

        if (category.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var categoryResource = CategoryResourceFromEntityAssembler.toResourceFromEntity(category.get());
        return ResponseEntity.ok(categoryResource);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CategoryResource>> getCategoriesByUserId(@PathVariable Long userId) {
        var query = new GetAllCategoriesByUserIdQuery(userId);
        var categories = categoryQueryService.handle(query);

        var categoryResources = categories.stream()
                .map(CategoryResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryResources);
    }
}
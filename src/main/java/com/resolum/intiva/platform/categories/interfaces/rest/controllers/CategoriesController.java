package com.resolum.intiva.platform.categories.interfaces.rest.controllers;

import com.resolum.intiva.platform.categories.domain.model.queries.GetAllCategoriesByOwnerTypeAndOwnerId;
import com.resolum.intiva.platform.categories.domain.model.queries.GetCategoryByIdQuery;
import com.resolum.intiva.platform.categories.domain.services.CategoryCommandService;
import com.resolum.intiva.platform.categories.domain.services.CategoryQueryService;
import com.resolum.intiva.platform.categories.interfaces.rest.assemblers.CategoryResourceFromEntityAssembler;
import com.resolum.intiva.platform.categories.interfaces.rest.assemblers.CreateCategoryCommandFromResourceAssembler;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.requests.CategoryFilterResource;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.requests.CreateCategoryResource;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.responses.CategoryResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing categories in the Intiva platform.
 * Provides endpoints for creating and retrieving categories based on various criteria.
 *
 * <p>
 * Endpoints:
 *
 * <li> POST /api/v1/categories: Create a new category.</li>
 * <li> GET /api/v1/categories/{id}: Retrieve a category by its unique identifier.</li>
 * <li> GET /api/v1/categories?ownerType={ownerType}&ownerId={ownerId}: Retrieve categories based on owner type and owner ID.</li>
 */
@RestController
@RequestMapping(value = "/api/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Categories", description = "Endpoints for managing categories")
public class CategoriesController {

    // CategoryCommandService is used to handle commands related to categories, such as creating a new category or updating an existing one.
    private final CategoryCommandService categoryCommandService;

    // CategoryQueryService is used to handle queries related to categories, such as retrieving category details or lists of categories based on specific criteria.
    private final CategoryQueryService categoryQueryService;

    // Constructor for CategoriesController, which initializes the command and query services.
    public CategoriesController(CategoryCommandService categoryCommandService, CategoryQueryService categoryQueryService) {
        this.categoryCommandService = categoryCommandService;
        this.categoryQueryService = categoryQueryService;
    }

    @Operation(
            summary = "Create a new category",
            description = "Creates a new category with the provided details.",
            tags = {"Categories"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category details",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CreateCategoryResource.class)
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Category created successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryResource.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
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

    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a category by its unique identifier.",
            tags = {"Categories"},
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "id",
                            description = "Unique identifier of the category",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category retrieved successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryResource.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Category not found"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
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

    @Operation(
            summary = "Get categories by owner type and owner ID",
            description = "Retrieves a list of categories based on the specified owner type and owner ID.",
            tags = {"Categories"},
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "ownerType",
                            description = "The type of user making the request (e.g., 'individual', 'family').",
                            required = true,
                            example = "family"
                    ),
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "ownerId",
                            description = "The unique identifier of the user/family making the request.",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Categories retrieved successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryResource.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data"
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    @GetMapping
    public ResponseEntity<List<CategoryResource>> getCategories(
            @ParameterObject CategoryFilterResource filter
    ) {
        if (filter.ownerType() == null || filter.ownerId() == null) {
            return ResponseEntity.badRequest().build();
        }

        var query = new GetAllCategoriesByOwnerTypeAndOwnerId(filter.ownerType(), filter.ownerId());
        var categories = categoryQueryService.handle(query);
        var categoryResources = categories
                .stream()
                .map(CategoryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(categoryResources);
    }
}
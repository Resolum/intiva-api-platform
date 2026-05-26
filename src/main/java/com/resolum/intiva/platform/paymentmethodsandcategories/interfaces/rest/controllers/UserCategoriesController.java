package com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.controllers;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries.GetAllCategoriesByUserIdQuery;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.services.CategoryQueryService;
import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.assemblers.CategoryResourceFromEntityAssembler;
import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.resources.responses.CategoryResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserCategoriesController is a REST controller that handles HTTP requests related to user categories management.
 * It provides an endpoint to retrieve all categories associated with a specific user.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Endpoints related to user categories management")
public class UserCategoriesController {

    /**
     * The CategoryQueryService is a service that provides methods to query category data.
     * It is used to handle queries related to categories, such as retrieving categories by user ID.
     */
    private final CategoryQueryService categoryQueryService;

    /**
     * Constructs a new UserCategoriesController with the specified CategoryQueryService.
     * @param categoryQueryService the service to handle category queries
     */
    public UserCategoriesController(CategoryQueryService categoryQueryService) {
        this.categoryQueryService = categoryQueryService;
    }

    /**
     * Handles HTTP GET requests to retrieve all categories associated with a specific user.
     * @param userId the ID of the user whose categories are to be retrieved
     * @return a ResponseEntity containing a list of CategoryResource objects representing the user's categories
     */
    @GetMapping("/{userId}/categories")
    @Operation(summary = "Get All Categories by User ID", description = "Retrieves all categories associated with a specific user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories for the user.")
    public ResponseEntity<List<CategoryResource>> getCategoriesByUserId(@PathVariable Long userId) {
        var query = new GetAllCategoriesByUserIdQuery(userId);
        var categories = categoryQueryService.handle(query);

        var categoryResources = categories.stream()
                .map(CategoryResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categoryResources);
    }
}

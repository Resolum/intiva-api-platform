package com.resolum.intiva.platform.household.interfaces.rest.controllers;

import com.resolum.intiva.platform.household.domain.model.queries.GetFamilyByIdQuery;
import com.resolum.intiva.platform.household.domain.services.FamilyCommandService;
import com.resolum.intiva.platform.household.domain.services.FamilyQueryService;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.CreateFamilyCommandFromResourceAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.FamilyResourceFromEntityAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.resources.requests.CreateFamilyResource;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.FamilyResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST controller for managing family groups.
 * Exposes endpoints for creating and retrieving family groups.
 */
@RestController
@RequestMapping(value = "/api/v1/group-families", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Family Group", description = "Endpoints related to family group management and member administration")
public class FamilyController {

    private final FamilyCommandService familyCommandService;
    private final FamilyQueryService familyQueryService;

    /**
     * Creates the controller with the required command and query services.
     *
     * @param familyCommandService command service dependency
     * @param familyQueryService   query service dependency
     */
    public FamilyController(FamilyCommandService familyCommandService, FamilyQueryService familyQueryService) {
        this.familyCommandService = familyCommandService;
        this.familyQueryService = familyQueryService;
    }

    /**
     * Creates a new family group and assigns the authenticated user as administrator.
     * The authenticated user's numeric ID (from JWT subject) is used as the owner.
     *
     * @param resource  request body with the family group details
     * @param principal authenticated user whose name is the numeric user ID from JWT
     * @return 201 with the created family group resource, or 400 if input is invalid
     */
    @PostMapping
    @Operation(
            summary = "Create a family group",
            description = "Creates a new family group and automatically assigns the authenticated user as the ADMIN member."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Family group created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<?> createFamily(
            @Valid @RequestBody CreateFamilyResource resource,
            Principal principal) {
        try {
            var ownerId = Long.parseLong(principal.getName());
            var command = CreateFamilyCommandFromResourceAssembler.toCommandFromResource(resource, ownerId);
            var family = familyCommandService.handle(command);
            var familyResource = FamilyResourceFromEntityAssembler.toResourceFromEntity(family);
            return new ResponseEntity<>(familyResource, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves a family group by its unique identifier.
     *
     * @param id the unique identifier of the family group
     * @return 200 with the family group resource, or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get family group by ID",
            description = "Retrieves a specific family group by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Family group found"),
            @ApiResponse(responseCode = "404", description = "Family group not found")
    })
    public ResponseEntity<FamilyResource> getFamilyById(@PathVariable Long id) {
        var query = new GetFamilyByIdQuery(id);
        var family = familyQueryService.handle(query);
        return family
                .map(f -> ResponseEntity.ok(FamilyResourceFromEntityAssembler.toResourceFromEntity(f)))
                .orElse(ResponseEntity.notFound().build());
    }
}

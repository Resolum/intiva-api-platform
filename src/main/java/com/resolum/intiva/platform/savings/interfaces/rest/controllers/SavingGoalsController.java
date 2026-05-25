package com.resolum.intiva.platform.savings.interfaces.rest.controllers;

import com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByUserIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetSavingGoalByIdQuery;
import com.resolum.intiva.platform.savings.domain.services.SavingGoalCommandService;
import com.resolum.intiva.platform.savings.domain.services.SavingGoalQueryService;
import com.resolum.intiva.platform.savings.interfaces.rest.assemblers.ContributeToSavingGoalCommandFromResourceAssembler;
import com.resolum.intiva.platform.savings.interfaces.rest.assemblers.SavingGoalResourceFromEntityAssembler;
import com.resolum.intiva.platform.savings.interfaces.rest.assemblers.CreateSavingGoalCommandFromResourceAssembler;
import com.resolum.intiva.platform.savings.interfaces.rest.resources.requests.ContributeToSavingGoalResource;
import com.resolum.intiva.platform.savings.interfaces.rest.resources.requests.CreateSavingGoalResource;
import com.resolum.intiva.platform.savings.interfaces.rest.resources.responses.SavingGoalResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing saving goals and contributions.
 * Exposes endpoints for creating, retrieving, and contributing to saving goals.
 */
@RestController
@RequestMapping("/api/v1/saving-goals")
@Tag(name = "Saving Goals", description = "Endpoints for managing saving goals and contributions")
public class SavingGoalsController {

    private final SavingGoalCommandService savingGoalCommandService;
    private final SavingGoalQueryService savingGoalQueryService;

    /**
     * Constructs a new SavingGoalsController with the required services.
     *
     * @param savingGoalCommandService the service for handling saving goal commands
     * @param savingGoalQueryService   the service for handling saving goal queries
     */
    public SavingGoalsController(SavingGoalCommandService savingGoalCommandService, SavingGoalQueryService savingGoalQueryService) {
        this.savingGoalCommandService = savingGoalCommandService;
        this.savingGoalQueryService = savingGoalQueryService;
    }
    /**
     * Registers a monetary contribution to an existing saving goal.
     *
     * @param savingGoalId the ID of the saving goal to contribute to
     * @param resource     the contribution details including amount and currency
     * @return 201 with the updated saving goal, 404 if not found, 400 if amount is invalid
     */
    @Operation(summary = "Contribute to a Saving Goal", description = "Registers a monetary contribution to a specific saving goal and updates the current saved amount.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contribution registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid contribution amount"),
            @ApiResponse(responseCode = "404", description = "Saving goal not found")
    })
    @PostMapping("/{savingGoalId}/contributions")
    public ResponseEntity<?> contribute(
            @PathVariable Long savingGoalId,
            @RequestBody ContributeToSavingGoalResource resource) {

        try {
            var command = ContributeToSavingGoalCommandFromResourceAssembler.toCommandFromResource(savingGoalId, resource);
            var savingGoalOpt = savingGoalCommandService.handle(command);

            if (savingGoalOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var savingGoalResource = SavingGoalResourceFromEntityAssembler.toResourceFromEntity(savingGoalOpt.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(savingGoalResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves a saving goal by its unique identifier.
     *
     * @param savingGoalId the ID of the saving goal to retrieve
     * @return 200 with the saving goal resource, or 404 if not found
     */
    @Operation(summary = "Get Saving Goal by ID", description = "Retrieves a specific saving goal by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saving goal found"),
            @ApiResponse(responseCode = "404", description = "Saving goal not found")
    })
    @GetMapping("/{savingGoalId}")
    public ResponseEntity<SavingGoalResource> getSavingGoalById(@PathVariable Long savingGoalId) {
        var query = new GetSavingGoalByIdQuery(savingGoalId);
        var savingGoalOpt = savingGoalQueryService.handle(query);

        if (savingGoalOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var savingGoalResource = SavingGoalResourceFromEntityAssembler.toResourceFromEntity(savingGoalOpt.get());
        return ResponseEntity.ok(savingGoalResource);
    }

    /**
     * Retrieves all saving goals belonging to a specific user.
     *
     * @param userId the ID of the user whose saving goals to retrieve
     * @return 200 with the list of saving goals for the given user
     */
    @Operation(summary = "Get All Saving Goals by User ID", description = "Retrieves all personal saving goals associated with a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saving goals retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<SavingGoalResource>> getAllSavingGoalsByUserId(@RequestParam Long userId) {
        var query = new GetAllSavingGoalsByUserIdQuery(userId);
        var savingGoals = savingGoalQueryService.handle(query);

        var resources = savingGoals.stream()
                .map(SavingGoalResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    /**
     * Creates a new saving goal for a user or family group.
     *
     * @param resource the details of the saving goal to create
     * @return 201 with the created saving goal, or 400 if the input data is invalid
     */
    @Operation(summary = "Create a Saving Goal", description = "Creates a new personal or group saving goal with a target amount and deadline.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saving goal created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<?> createSavingGoal(@RequestBody CreateSavingGoalResource resource) {
        try {
            var command = CreateSavingGoalCommandFromResourceAssembler.toCommandFromResource(resource);
            var savingGoal = savingGoalCommandService.handle(command);
            var savingGoalResource = SavingGoalResourceFromEntityAssembler.toResourceFromEntity(savingGoal);
            return ResponseEntity.status(HttpStatus.CREATED).body(savingGoalResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all saving goals belonging to a specific family group.
     *
     * @param groupId the ID of the group whose saving goals to retrieve
     * @return 200 with the list of saving goals for the given group
     */
    @Operation(summary = "Get All Saving Goals by Group ID", description = "Retrieves all shared saving goals associated with a specific family group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group saving goals retrieved successfully")
    })
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<SavingGoalResource>> getAllSavingGoalsByGroupId(@PathVariable String groupId) {
        var query = new com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByGroupIdQuery(groupId);
        var savingGoals = savingGoalQueryService.handle(query);

        var resources = savingGoals.stream()
                .map(SavingGoalResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }
}

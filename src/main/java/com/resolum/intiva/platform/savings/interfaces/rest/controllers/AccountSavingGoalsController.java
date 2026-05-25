package com.resolum.intiva.platform.savings.interfaces.rest.controllers;

import com.resolum.intiva.platform.savings.domain.model.commands.CompleteSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.ContributeToSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.CreateSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.commands.UncompleteSavingGoalCommand;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllCompletedSavingGoalsByUserIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByUserIdQuery;
import com.resolum.intiva.platform.savings.domain.model.queries.GetSavingGoalByIdQuery;
import com.resolum.intiva.platform.savings.domain.services.SavingGoalCommandService;
import com.resolum.intiva.platform.savings.domain.services.SavingGoalQueryService;
import com.resolum.intiva.platform.savings.interfaces.rest.assemblers.ContributeToSavingGoalCommandFromResourceAssembler;
import com.resolum.intiva.platform.savings.interfaces.rest.assemblers.CreateSavingGoalCommandFromResourceAssembler;
import com.resolum.intiva.platform.savings.interfaces.rest.assemblers.SavingGoalResourceFromEntityAssembler;
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
 * REST controller for managing saving goals scoped to a specific account.
 * All endpoints are nested under /api/v1/accounts/{accountId}/saving-goals.
 * The accountId is used as the actorUserId for personal goals and as the contributorId for contributions.
 */
@RestController
@RequestMapping("/api/v1/accounts/{accountId}/saving-goals")
@Tag(name = "Accounts", description = "Available Account Endpoints")
public class AccountSavingGoalsController {

    private final SavingGoalCommandService savingGoalCommandService;
    private final SavingGoalQueryService savingGoalQueryService;

    /**
     * Constructs a new AccountSavingGoalsController with the required services.
     *
     * @param savingGoalCommandService the service for handling saving goal commands
     * @param savingGoalQueryService   the service for handling saving goal queries
     */
    public AccountSavingGoalsController(SavingGoalCommandService savingGoalCommandService, SavingGoalQueryService savingGoalQueryService) {
        this.savingGoalCommandService = savingGoalCommandService;
        this.savingGoalQueryService = savingGoalQueryService;
    }

    /**
     * Retrieves all saving goals associated with a specific account.
     *
     * @param accountId the ID of the account whose saving goals to retrieve
     * @return 200 with the list of saving goals for the given account
     */
    @Operation(summary = "Get All Saving Goals by Account ID", description = "Retrieves all saving goals associated with a specific account ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saving goals retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<SavingGoalResource>> getAllSavingGoalsByUserId(@PathVariable Long accountId) {
        var query = new GetAllSavingGoalsByUserIdQuery(accountId);
        var savingGoals = savingGoalQueryService.handle(query);

        var resources = savingGoals.stream()
                .map(SavingGoalResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    /**
     * Creates a new saving goal for the specified account.
     * The actorUserId is automatically set to the provided accountId.
     *
     * @param accountId the ID of the account creating the saving goal
     * @param resource  the details of the saving goal to create
     * @return 201 with the created saving goal, or 400 if the input data is invalid
     */
    @Operation(summary = "Create Saving Goal for Account", description = "Creates a new saving goal associated with the specified account. The actorUserId is automatically set to the accountId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saving goal created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<?> createSavingGoal(
            @PathVariable Long accountId,
            @RequestBody CreateSavingGoalResource resource) {
        try {
            var initialCommand = CreateSavingGoalCommandFromResourceAssembler.toCommandFromResource(resource);
            var command = new CreateSavingGoalCommand(
                    initialCommand.ownerType(),
                    accountId, // force actorUserId = accountId
                    initialCommand.ownerId(),
                    initialCommand.title(),
                    initialCommand.targetAmount(),
                    initialCommand.currencyCode(),
                    initialCommand.description(),
                    initialCommand.startsAt(),
                    initialCommand.deadline(),
                    initialCommand.categoryId()
            );

            var savingGoal = savingGoalCommandService.handle(command);
            var savingGoalResource = SavingGoalResourceFromEntityAssembler.toResourceFromEntity(savingGoal);
            return ResponseEntity.status(HttpStatus.CREATED).body(savingGoalResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves a specific saving goal by ID, only if it belongs to the given account.
     *
     * @param accountId    the ID of the account that owns the saving goal
     * @param savingGoalId the ID of the saving goal to retrieve
     * @return 200 with the saving goal, or 404 if not found or not owned by the account
     */
    @Operation(summary = "Get Saving Goal by ID for Account", description = "Retrieves a specific saving goal by its ID, only if it belongs to the specified account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saving goal found"),
            @ApiResponse(responseCode = "404", description = "Saving goal not found or does not belong to this account")
    })
    @GetMapping("/{savingGoalId}")
    public ResponseEntity<SavingGoalResource> getSavingGoalById(
            @PathVariable Long accountId,
            @PathVariable Long savingGoalId) {
        var query = new GetSavingGoalByIdQuery(savingGoalId);
        var savingGoalOpt = savingGoalQueryService.handle(query);

        if (savingGoalOpt.isEmpty() || !accountId.equals(savingGoalOpt.get().getActorUserId())) {
            return ResponseEntity.notFound().build();
        }

        var savingGoalResource = SavingGoalResourceFromEntityAssembler.toResourceFromEntity(savingGoalOpt.get());
        return ResponseEntity.ok(savingGoalResource);
    }

    /**
     * Registers a monetary contribution to a saving goal under the specified account.
     * The contributorId is automatically set to the provided accountId.
     *
     * @param accountId    the ID of the account making the contribution
     * @param savingGoalId the ID of the saving goal to contribute to
     * @param resource     the contribution details including amount and currency
     * @return 201 with the updated saving goal, 404 if not found, 400 if amount is invalid
     */
    @Operation(summary = "Contribute to Saving Goal for Account", description = "Registers a monetary contribution to a saving goal. The contributorId is automatically set to the accountId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contribution registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid contribution amount"),
            @ApiResponse(responseCode = "404", description = "Saving goal not found")
    })
    @PostMapping("/{savingGoalId}/contributions")
    public ResponseEntity<?> contribute(
            @PathVariable Long accountId,
            @PathVariable Long savingGoalId,
            @RequestBody ContributeToSavingGoalResource resource) {

        try {
            var initialCommand = ContributeToSavingGoalCommandFromResourceAssembler.toCommandFromResource(savingGoalId, resource);
            var command = new ContributeToSavingGoalCommand(
                    initialCommand.savingGoalId(),
                    initialCommand.amount(),
                    initialCommand.currencyCode(),
                    accountId // force contributorId = accountId
            );

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
     * Retrieves all saving goals belonging to a specific family group under this account.
     *
     * @param accountId the ID of the account making the request
     * @param groupId   the ID of the family group whose saving goals to retrieve
     * @return 200 with the list of group saving goals
     */
    @Operation(summary = "Get All Group Saving Goals by Account", description = "Retrieves all shared saving goals associated with a specific family group, accessible from the account context.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group saving goals retrieved successfully")
    })
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<SavingGoalResource>> getAllSavingGoalsByGroupId(
            @PathVariable Long accountId,
            @PathVariable String groupId) {
        var query = new com.resolum.intiva.platform.savings.domain.model.queries.GetAllSavingGoalsByGroupIdQuery(groupId);
        var savingGoals = savingGoalQueryService.handle(query);

        var resources = savingGoals.stream()
                .map(SavingGoalResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    /**
     * Marks an existing saving goal as completed for the specified account.
     *
     * @param accountId    the ID of the account that owns the saving goal
     * @param savingGoalId the ID of the saving goal to complete
     * @return 200 with the updated saving goal, 400 if already completed, 404 if not found
     */
    @Operation(
            summary = "Complete a Saving Goal by Account",
            description = "Marks a saving goal as COMPLETED for the specified account. Returns 400 if it is already completed and 404 if it does not exist."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saving goal marked as completed successfully"),
            @ApiResponse(responseCode = "400", description = "Saving goal is already completed"),
            @ApiResponse(responseCode = "404", description = "Saving goal not found")
    })
    @PatchMapping("/{savingGoalId}/complete")
    public ResponseEntity<?> completeSavingGoal(
            @PathVariable Long accountId,
            @PathVariable Long savingGoalId) {
        try {
            var command = new CompleteSavingGoalCommand(savingGoalId);
            var savingGoal = savingGoalCommandService.handle(command);
            var savingGoalResource = SavingGoalResourceFromEntityAssembler.toResourceFromEntity(savingGoal);
            return ResponseEntity.ok(savingGoalResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Reverts an existing saving goal back to uncompleted status for the specified account.
     *
     * @param accountId    the ID of the account that owns the saving goal
     * @param savingGoalId the ID of the saving goal to uncomplete
     * @return 200 with the updated saving goal, 400 if already uncompleted, 404 if not found
     */
    @Operation(
            summary = "Uncomplete a Saving Goal by Account",
            description = "Reverts a saving goal to UNCOMPLETED status for the specified account. Returns 400 if it is already uncompleted and 404 if it does not exist."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saving goal reverted to uncompleted successfully"),
            @ApiResponse(responseCode = "400", description = "Saving goal is already marked as uncompleted"),
            @ApiResponse(responseCode = "404", description = "Saving goal not found")
    })
    @PatchMapping("/{savingGoalId}/uncomplete")
    public ResponseEntity<?> uncompleteSavingGoal(
            @PathVariable Long accountId,
            @PathVariable Long savingGoalId) {
        try {
            var command = new UncompleteSavingGoalCommand(savingGoalId);
            var savingGoal = savingGoalCommandService.handle(command);
            var savingGoalResource = SavingGoalResourceFromEntityAssembler.toResourceFromEntity(savingGoal);
            return ResponseEntity.ok(savingGoalResource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all completed saving goals for the specified account.
     *
     * @param accountId the ID of the account whose completed saving goals to retrieve
     * @return 200 with the list of completed saving goals
     */
    @Operation(
            summary = "Get All Completed Saving Goals by Account ID",
            description = "Retrieves all saving goals with COMPLETED status for a specific account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Completed saving goals retrieved successfully")
    })
    @GetMapping("/completed")
    public ResponseEntity<List<SavingGoalResource>> getCompletedSavingGoals(@PathVariable Long accountId) {
        var query = new GetAllCompletedSavingGoalsByUserIdQuery(accountId);
        var savingGoals = savingGoalQueryService.handle(query);

        var resources = savingGoals.stream()
                .map(SavingGoalResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }
}

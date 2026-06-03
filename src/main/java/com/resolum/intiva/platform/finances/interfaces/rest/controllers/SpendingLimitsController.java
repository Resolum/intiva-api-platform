package com.resolum.intiva.platform.finances.interfaces.rest.controllers;

import com.resolum.intiva.platform.finances.domain.model.commands.ActivateSpendingLimitCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.DeactivateSpendingLimitCommand;
import com.resolum.intiva.platform.finances.domain.model.queries.*;
import com.resolum.intiva.platform.finances.domain.services.SpendingLimitCommandService;
import com.resolum.intiva.platform.finances.domain.services.SpendingLimitQueryService;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.*;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.CreateSpendingLimitResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.UpdateSpendingLimitAmountResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.UpdateSpendingLimitPeriodResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.SpendingLimitResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageWrapperResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for creating, querying and maintaining spending limits.
 *
 * <p>The same API serves both personal and family finances by using {@code ownerType} to distinguish
 * INDIVIDUAL from FAMILY ownership.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/spending-limits", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(
        name = "Spending Limits",
        description = "Endpoints for personal and family expense limits by category or financial account."
)
public class SpendingLimitsController {

    /**
     * Command service used for create and maintenance operations.
     */
    private final SpendingLimitCommandService spendingLimitCommandService;

    /**
     * Query service used for retrieval operations.
     */
    private final SpendingLimitQueryService spendingLimitQueryService;

    /**
     * Creates the spending limit controller with command and query services.
     *
     * @param spendingLimitCommandService command service dependency
     * @param spendingLimitQueryService query service dependency
     */
    public SpendingLimitsController(
            SpendingLimitCommandService spendingLimitCommandService,
            SpendingLimitQueryService spendingLimitQueryService
    ) {
        this.spendingLimitCommandService = spendingLimitCommandService;
        this.spendingLimitQueryService = spendingLimitQueryService;
    }

    /**
     * Creates a new spending limit.
     */
    @PostMapping
    @Operation(
            summary = "Create a spending limit",
            description = """
                    Creates an expense limit inside the finances bounded context.
                    
                    The same endpoint supports personal and group finances:
                    - ownerType=INDIVIDUAL means ownerId is a user id.
                    - ownerType=FAMILY means ownerId is a family/group id.
                    
                    The target defines what is controlled:
                    - targetType=CATEGORY consumes the limit when an EXPENSE uses that category.
                    - targetType=FINANCIAL_ACCOUNT consumes the limit when an EXPENSE uses that payment method/account.
                    
                    The limitAmount is fixed; spentAmount starts at zero and increases automatically when matching EXPENSE transactions are registered.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Spending limit created successfully",
                    content = @Content(schema = @Schema(implementation = SpendingLimitResource.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid owner, target, amount, currency, or period")
    })
    public ResponseEntity<?> createSpendingLimit(
            @RequestBody(
                    description = "Spending limit to create. Use INDIVIDUAL for personal finances and FAMILY for group finances.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateSpendingLimitResource.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Personal category limit",
                                            summary = "Limit a user's expenses in one category",
                                            value = """
                                                    {
                                                      "ownerId": 1,
                                                      "ownerType": "INDIVIDUAL",
                                                      "targetType": "CATEGORY",
                                                      "targetId": 5,
                                                      "limitAmount": 500.00,
                                                      "currencyCode": "PEN",
                                                      "startDate": "2026-06-01",
                                                      "endDate": "2026-06-30"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Family payment method limit",
                                            summary = "Limit a family/group's expenses through one financial account",
                                            value = """
                                                    {
                                                      "ownerId": 10,
                                                      "ownerType": "FAMILY",
                                                      "targetType": "FINANCIAL_ACCOUNT",
                                                      "targetId": 12,
                                                      "limitAmount": 2000.00,
                                                      "currencyCode": "PEN",
                                                      "startDate": "2026-06-01",
                                                      "endDate": "2026-06-30"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody CreateSpendingLimitResource resource
    ) {
        try {
            var command = CreateSpendingLimitCommandFromResourceAssembler.toCommandFromResource(resource);
            var spendingLimit = spendingLimitCommandService.handle(command);
            var response = SpendingLimitResourceFromEntityAssembler.toResourceFromEntity(spendingLimit.get());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResource(e.getMessage()));
        }
    }

    /**
     * Retrieves a spending limit by identifier.
     */
    @GetMapping("/{spendingLimitId}")
    @Operation(
            summary = "Get a spending limit by ID",
            description = "Retrieves one spending limit with its configured limitAmount, consumed spentAmount, active flag, and status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending limit found", content = @Content(schema = @Schema(implementation = SpendingLimitResource.class))),
            @ApiResponse(responseCode = "404", description = "Spending limit not found")
    })
    public ResponseEntity<SpendingLimitResource> getSpendingLimitById(
            @Parameter(description = "Spending limit identifier.", example = "1")
            @PathVariable Long spendingLimitId
    ) {
        var spendingLimit = spendingLimitQueryService.handle(new GetSpendingLimitByIdQuery(spendingLimitId));
        return spendingLimit
                .map(limit -> ResponseEntity.ok(SpendingLimitResourceFromEntityAssembler.toResourceFromEntity(limit)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves the spending limits for one owner.
     */
    @GetMapping
    @Operation(
            summary = "Get spending limits by owner",
            description = """
                    Retrieves all spending limits for an owner.
                    
                    Use ownerType=INDIVIDUAL for personal finance limits and ownerType=FAMILY for group/family limits.
                    Optional query parameters can filter the result by target type or by a specific target id.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending limits retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "ownerId is required, ownerType is invalid, or targetId was sent without targetType")
    })
    public ResponseEntity<MessageWrapperResponse<List<SpendingLimitResource>>> getSpendingLimits(
            @Parameter(in = ParameterIn.QUERY, description = "Owner identifier. For INDIVIDUAL it is the user id; for FAMILY it is the family/group id.", example = "1", required = true)
            @RequestParam(name = "ownerId", required = false) Long ownerId,
            @Parameter(in = ParameterIn.QUERY, description = "Optional owner scope filter.", example = "INDIVIDUAL", schema = @Schema(allowableValues = {"INDIVIDUAL", "FAMILY"}))
            @RequestParam(name = "ownerType", required = false) String ownerType,
            @Parameter(in = ParameterIn.QUERY, description = "Optional target type filter.", example = "CATEGORY", schema = @Schema(allowableValues = {"CATEGORY", "FINANCIAL_ACCOUNT"}))
            @RequestParam(name = "targetType", required = false) String targetType,
            @Parameter(in = ParameterIn.QUERY, description = "Optional target identifier filter. Requires targetType.", example = "5")
            @RequestParam(name = "targetId", required = false) Long targetId
    ) {
        if (ownerId == null) {
            return ResponseEntity.badRequest().build();
        }
        if (targetId != null && targetType == null) {
            return ResponseEntity.badRequest().body(new MessageWrapperResponse<>(
                    "targetType is required when targetId is provided.",
                    List.<SpendingLimitResource>of()
            ));
        }
        try {
            var parsedOwnerType = ownerType == null
                    ? null
                    : OwnerTypes.valueOf(ownerType.toUpperCase());

            var parsedTargetType = targetType == null
                    ? null
                    : SpendingLimitTargetType.valueOf(targetType.toUpperCase());

            var limits = parsedOwnerType == null
                    ? spendingLimitQueryService.handle(new GetSpendingLimitsByOwnerIdQuery(ownerId))
                    : spendingLimitQueryService.handle(new GetSpendingLimitsByOwnerIdAndOwnerTypeQuery(ownerId, parsedOwnerType));

            var filteredLimits = limits.stream()
                    .filter(limit -> parsedTargetType == null || limit.getTargetType() == parsedTargetType)
                    .filter(limit -> targetId == null || limit.getTargetId().equals(targetId))
                    .toList();

            var resources = filteredLimits.stream()
                    .map(SpendingLimitResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();

            return ResponseEntity.ok(new MessageWrapperResponse<>("Spending limits retrieved successfully.", resources));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageWrapperResponse<>(
                    "Invalid ownerType or targetType.",
                    List.<SpendingLimitResource>of()
            ));
        }
    }

    /**
     * Updates the maximum amount of an existing spending limit.
     */
    @PatchMapping("/{spendingLimitId}/amount")
    @Operation(
            summary = "Update spending limit amount",
            description = "Updates the configured maximum amount. The currency must match the spending limit's original currency."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending limit amount updated successfully", content = @Content(schema = @Schema(implementation = SpendingLimitResource.class))),
            @ApiResponse(responseCode = "400", description = "Spending limit not found, invalid amount, or currency mismatch")
    })
    public ResponseEntity<?> updateSpendingLimitAmount(
            @Parameter(description = "Spending limit identifier.", example = "1")
            @PathVariable Long spendingLimitId,
            @RequestBody(
                    description = "New amount for the spending limit.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateSpendingLimitAmountResource.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "limitAmount": 700.00,
                                      "currencyCode": "PEN"
                                    }
                                    """)
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody UpdateSpendingLimitAmountResource resource
    ) {
        try {
            var command = UpdateSpendingLimitAmountCommandFromResourceAssembler.toCommandFromResource(spendingLimitId, resource);
            var spendingLimit = spendingLimitCommandService.handle(command);
            return ResponseEntity.ok(SpendingLimitResourceFromEntityAssembler.toResourceFromEntity(spendingLimit.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResource(e.getMessage()));
        }
    }

    /**
     * Updates the active period of an existing spending limit.
     */
    @PatchMapping("/{spendingLimitId}/period")
    @Operation(
            summary = "Update spending limit period",
            description = "Updates the inclusive start and end dates used to decide whether an EXPENSE consumes the limit."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending limit period updated successfully", content = @Content(schema = @Schema(implementation = SpendingLimitResource.class))),
            @ApiResponse(responseCode = "400", description = "Spending limit not found or invalid period")
    })
    public ResponseEntity<?> updateSpendingLimitPeriod(
            @Parameter(description = "Spending limit identifier.", example = "1")
            @PathVariable Long spendingLimitId,
            @RequestBody(
                    description = "New inclusive date range for the spending limit.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateSpendingLimitPeriodResource.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "startDate": "2026-07-01",
                                      "endDate": "2026-07-31"
                                    }
                                    """)
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody UpdateSpendingLimitPeriodResource resource
    ) {
        try {
            var command = UpdateSpendingLimitPeriodCommandFromResourceAssembler.toCommandFromResource(spendingLimitId, resource);
            var spendingLimit = spendingLimitCommandService.handle(command);
            return ResponseEntity.ok(SpendingLimitResourceFromEntityAssembler.toResourceFromEntity(spendingLimit.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResource(e.getMessage()));
        }
    }

    /**
     * Activates a spending limit.
     */
    @PatchMapping("/{spendingLimitId}/activate")
    @Operation(
            summary = "Activate a spending limit",
            description = "Re-enables a spending limit so future matching EXPENSE transactions consume its spentAmount."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending limit activated successfully", content = @Content(schema = @Schema(implementation = SpendingLimitResource.class))),
            @ApiResponse(responseCode = "400", description = "Spending limit not found")
    })
    public ResponseEntity<?> activateSpendingLimit(
            @Parameter(description = "Spending limit identifier.", example = "1")
            @PathVariable Long spendingLimitId
    ) {
        try {
            var spendingLimit = spendingLimitCommandService.handle(new ActivateSpendingLimitCommand(spendingLimitId));
            return ResponseEntity.ok(SpendingLimitResourceFromEntityAssembler.toResourceFromEntity(spendingLimit.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResource(e.getMessage()));
        }
    }

    /**
     * Deactivates a spending limit.
     */
    @PatchMapping("/{spendingLimitId}/deactivate")
    @Operation(
            summary = "Deactivate a spending limit",
            description = "Disables a spending limit. Matching EXPENSE transactions no longer consume its spentAmount while it is inactive."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spending limit deactivated successfully", content = @Content(schema = @Schema(implementation = SpendingLimitResource.class))),
            @ApiResponse(responseCode = "400", description = "Spending limit not found")
    })
    public ResponseEntity<?> deactivateSpendingLimit(
            @Parameter(description = "Spending limit identifier.", example = "1")
            @PathVariable Long spendingLimitId
    ) {
        try {
            var spendingLimit = spendingLimitCommandService.handle(new DeactivateSpendingLimitCommand(spendingLimitId));
            return ResponseEntity.ok(SpendingLimitResourceFromEntityAssembler.toResourceFromEntity(spendingLimit.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResource(e.getMessage()));
        }
    }

}

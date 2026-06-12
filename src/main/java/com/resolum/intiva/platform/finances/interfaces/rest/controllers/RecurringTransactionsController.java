package com.resolum.intiva.platform.finances.interfaces.rest.controllers;

import com.resolum.intiva.platform.finances.domain.model.commands.ActivateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.DeactivateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdatePaymentReminderCommand;
import com.resolum.intiva.platform.finances.domain.model.queries.GetRecurringTransactionByIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetRecurringTransactionsByOwnerIdAndOwnerTypeQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetRecurringTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.services.RecurringTransactionCommandService;
import com.resolum.intiva.platform.finances.domain.services.RecurringTransactionQueryService;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.CreateRecurringTransactionCommandFromResourceAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.RecurringTransactionResourceFromEntityAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.CreateRecurringTransactionResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.UpdateRecurringTransactionReminderResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.RecurringTransactionResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageWrapperResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for recurring transaction definitions.
 *
 * <p>Recurring definitions stay inside the finances bounded context because their responsibility is to materialize
 * future transactions using the same business flow as manually registered transactions.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/recurring-transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Recurring Transactions", description = "Endpoints related to recurring incomes and recurring expenses")
@Slf4j
public class RecurringTransactionsController {

    /**
     * Command service used to create and toggle recurring definitions.
     */
    private final RecurringTransactionCommandService recurringTransactionCommandService;

    /**
     * Query service used to retrieve recurring definitions.
     */
    private final RecurringTransactionQueryService recurringTransactionQueryService;

    /**
     * Creates the controller with its command and query dependencies.
     *
     * @param recurringTransactionCommandService command service dependency
     * @param recurringTransactionQueryService query service dependency
     */
    public RecurringTransactionsController(
            RecurringTransactionCommandService recurringTransactionCommandService,
            RecurringTransactionQueryService recurringTransactionQueryService
    ) {
        this.recurringTransactionCommandService = recurringTransactionCommandService;
        this.recurringTransactionQueryService = recurringTransactionQueryService;
    }

    /**
     * Creates a recurring transaction definition.
     *
     * @param resource request payload describing the recurring definition
     * @return created recurring transaction definition
     */
    @PostMapping
    @Operation(
            summary = "Create a recurring transaction",
            description = """
                    Creates a recurring income or recurring expense definition.

                    The definition is stored inside the finances bounded context and later executed by a backend scheduler.
                    Each execution generates a normal transaction, so existing validations, balance updates and spending-limit
                    consumption continue to work through the same transaction flow.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurring transaction created successfully", content = @Content(schema = @Schema(implementation = RecurringTransactionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid recurring transaction data"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public ResponseEntity<?> createRecurringTransaction(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Recurring transaction definition to create.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateRecurringTransactionResource.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Monthly salary",
                                            summary = "Personal recurring income executed every month",
                                            value = """
                                                    {
                                                      "amount": 1500.00,
                                                      "currencyCode": "PEN",
                                                      "description": "Sueldo",
                                                      "ownerId": 1,
                                                      "financialAccountId": 3,
                                                      "performedByUserId": 1,
                                                      "transactionType": "INCOME",
                                                      "categoryId": 5,
                                                      "ownerType": "INDIVIDUAL",
                                                      "frequency": "MONTHLY",
                                                      "startDate": "2026-06-15",
                                                      "endDate": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Daily family expense",
                                            summary = "Family recurring expense executed every day",
                                            value = """
                                                    {
                                                      "amount": 180.00,
                                                      "currencyCode": "PEN",
                                                      "description": "Compra recurrente del hogar",
                                                      "ownerId": 10,
                                                      "financialAccountId": 12,
                                                      "performedByUserId": 4,
                                                      "transactionType": "EXPENSE",
                                                      "categoryId": 8,
                                                      "ownerType": "FAMILY",
                                                      "frequency": "DAILY",
                                                      "startDate": "2026-06-07",
                                                      "endDate": "2026-12-27"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody CreateRecurringTransactionResource resource
    ) {
        log.info("POST /api/v1/recurring-transactions - Creating recurring transaction. description={}", resource.description());
        try {
            var command = CreateRecurringTransactionCommandFromResourceAssembler.toCommandFromResource(resource);
            var recurringTransaction = recurringTransactionCommandService.handle(command);
            log.info("Recurring transaction created successfully. id={}", recurringTransaction.get().getId());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(RecurringTransactionResourceFromEntityAssembler.toResourceFromEntity(recurringTransaction.get()));
        } catch (IllegalArgumentException exception) {
            log.warn("Bad request creating recurring transaction: {}", exception.getMessage());
            return ResponseEntity.badRequest().body(new MessageResource(exception.getMessage()));
        } catch (Exception exception) {
            log.error("Unexpected error creating recurring transaction", exception);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResource("Unexpected server error."));
        }
    }

    /**
     * Retrieves one recurring transaction definition by id.
     *
     * @param recurringTransactionId recurring transaction identifier
     * @return recurring transaction resource if found
     */
    @GetMapping("/{recurringTransactionId}")
    @Operation(summary = "Get recurring transaction by ID", description = "Retrieves a recurring transaction definition by its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurring transaction found", content = @Content(schema = @Schema(implementation = RecurringTransactionResource.class))),
            @ApiResponse(responseCode = "404", description = "Recurring transaction not found")
    })
    public ResponseEntity<RecurringTransactionResource> getRecurringTransactionById(
            @PathVariable Long recurringTransactionId
    ) {
        log.info("GET /api/v1/recurring-transactions/{}", recurringTransactionId);
        var recurringTransaction = recurringTransactionQueryService.handle(new GetRecurringTransactionByIdQuery(recurringTransactionId));
        if (recurringTransaction.isPresent()) {
            log.info("Recurring transaction found. id={}", recurringTransactionId);
            return ResponseEntity.ok(RecurringTransactionResourceFromEntityAssembler.toResourceFromEntity(recurringTransaction.get()));
        }
        log.warn("Recurring transaction not found. id={}", recurringTransactionId);
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves recurring transaction definitions filtered by owner id and optional owner type.
     *
     * @param ownerId owner identifier
     * @param ownerType optional owner scope filter
     * @return matching recurring transaction definitions
     */
    @GetMapping
    @Operation(
            summary = "Get recurring transactions",
            description = """
                    Retrieves recurring transaction definitions for one owner.

                    ownerId is required.
                    ownerType is optional and narrows the search to INDIVIDUAL or FAMILY.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurring transactions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<MessageWrapperResponse<List<RecurringTransactionResource>>> getRecurringTransactions(
            @Parameter(in = ParameterIn.QUERY, description = "Owner identifier used to filter recurring definitions.", required = true, example = "1")
            @RequestParam(name = "ownerId") Long ownerId,
            @Parameter(in = ParameterIn.QUERY, description = "Optional owner scope filter.", example = "INDIVIDUAL", schema = @Schema(allowableValues = {"INDIVIDUAL", "FAMILY"}))
            @RequestParam(name = "ownerType", required = false) String ownerType
    ) {
        log.info("GET /api/v1/recurring-transactions?ownerId={}&ownerType={}", ownerId, ownerType);
        try {
            var recurringTransactions = ownerType == null
                    ? recurringTransactionQueryService.handle(new GetRecurringTransactionsByOwnerIdQuery(ownerId))
                    : recurringTransactionQueryService.handle(new GetRecurringTransactionsByOwnerIdAndOwnerTypeQuery(
                    ownerId,
                    OwnerTypes.valueOf(ownerType.toUpperCase())
            ));

            var resources = recurringTransactions.stream()
                    .map(RecurringTransactionResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();

            log.info("Found {} recurring transactions for ownerId={}", resources.size(), ownerId);
            return ResponseEntity.ok(new MessageWrapperResponse<>(
                    recurringTransactions.isEmpty()
                            ? "No recurring transactions found for the provided criteria."
                            : "Recurring transactions retrieved successfully.",
                    resources
            ));
        } catch (IllegalArgumentException exception) {
            log.warn("Bad request querying recurring transactions: {}", exception.getMessage());
            return ResponseEntity.badRequest().body(new MessageWrapperResponse<>(
                    exception.getMessage(),
                    List.of()
            ));
        }
    }

    /**
     * Activates a recurring transaction definition so the scheduler can execute it again.
     *
     * @param recurringTransactionId recurring transaction identifier
     * @return updated recurring transaction resource
     */
    @PatchMapping("/{recurringTransactionId}/activate")
    @Operation(summary = "Activate recurring transaction", description = "Reactivates a recurring transaction definition.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurring transaction activated successfully", content = @Content(schema = @Schema(implementation = RecurringTransactionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid recurring transaction id")
    })
    public ResponseEntity<?> activateRecurringTransaction(@PathVariable Long recurringTransactionId) {
        log.info("PATCH /api/v1/recurring-transactions/{}/activate", recurringTransactionId);
        try {
            var recurringTransaction = recurringTransactionCommandService.handle(
                    new ActivateRecurringTransactionCommand(recurringTransactionId)
            );
            log.info("Recurring transaction activated. id={}", recurringTransactionId);
            return ResponseEntity.ok(RecurringTransactionResourceFromEntityAssembler.toResourceFromEntity(recurringTransaction.get()));
        } catch (IllegalArgumentException exception) {
            log.warn("Bad request activating recurring transaction {}: {}", recurringTransactionId, exception.getMessage());
            return ResponseEntity.badRequest().body(new MessageResource(exception.getMessage()));
        }
    }

    /**
     * Deactivates a recurring transaction definition so the scheduler stops executing it.
     *
     * @param recurringTransactionId recurring transaction identifier
     * @return updated recurring transaction resource
     */
    @PatchMapping("/{recurringTransactionId}/deactivate")
    @Operation(summary = "Deactivate recurring transaction", description = "Deactivates a recurring transaction definition.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurring transaction deactivated successfully", content = @Content(schema = @Schema(implementation = RecurringTransactionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid recurring transaction id")
    })
    public ResponseEntity<?> deactivateRecurringTransaction(@PathVariable Long recurringTransactionId) {
        log.info("PATCH /api/v1/recurring-transactions/{}/deactivate", recurringTransactionId);
        try {
            var recurringTransaction = recurringTransactionCommandService.handle(
                    new DeactivateRecurringTransactionCommand(recurringTransactionId)
            );
            log.info("Recurring transaction deactivated. id={}", recurringTransactionId);
            return ResponseEntity.ok(RecurringTransactionResourceFromEntityAssembler.toResourceFromEntity(recurringTransaction.get()));
        } catch (IllegalArgumentException exception) {
            log.warn("Bad request deactivating recurring transaction {}: {}", recurringTransactionId, exception.getMessage());
            return ResponseEntity.badRequest().body(new MessageResource(exception.getMessage()));
        }
    }

    /**
     * Updates the payment reminder configuration for a recurring transaction definition.
     *
     * @param recurringTransactionId recurring transaction identifier
     * @param resource               request payload with the new reminder days value
     * @return updated recurring transaction resource
     */
    @PatchMapping("/{recurringTransactionId}/reminder")
    @Operation(summary = "Update payment reminder", description = "Updates how many days before the end date a payment reminder is sent. Allowed values: 1, 3, 7.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reminder configuration updated successfully", content = @Content(schema = @Schema(implementation = RecurringTransactionResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid recurring transaction id or reminder value")
    })
    public ResponseEntity<?> updateRecurringTransactionReminder(
            @PathVariable Long recurringTransactionId,
            @RequestBody UpdateRecurringTransactionReminderResource resource
    ) {
        log.info("PATCH /api/v1/recurring-transactions/{}/reminder - reminderDaysBefore={}",
                recurringTransactionId, resource.reminderDaysBefore());
        try {
            var recurringTransaction = recurringTransactionCommandService.handle(
                    new UpdatePaymentReminderCommand(recurringTransactionId, resource.reminderDaysBefore())
            );
            log.info("Payment reminder updated. id={}, reminderDaysBefore={}",
                    recurringTransactionId, resource.reminderDaysBefore());
            return ResponseEntity.ok(RecurringTransactionResourceFromEntityAssembler.toResourceFromEntity(recurringTransaction.get()));
        } catch (IllegalArgumentException exception) {
            log.warn("Bad request updating reminder for recurring transaction {}: {}", recurringTransactionId, exception.getMessage());
            return ResponseEntity.badRequest().body(new MessageResource(exception.getMessage()));
        }
    }
}

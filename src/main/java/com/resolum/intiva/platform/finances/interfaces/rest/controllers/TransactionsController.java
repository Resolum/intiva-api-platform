package com.resolum.intiva.platform.finances.interfaces.rest.controllers;

import com.resolum.intiva.platform.categories.domain.model.exceptions.FinancialAccountSyncConflictException;
import com.resolum.intiva.platform.categories.domain.model.exceptions.InsufficientFundsException;
import com.resolum.intiva.platform.finances.domain.model.queries.GetLastTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionByIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdAndTransactionTypeQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.services.TransactionCommandService;
import com.resolum.intiva.platform.finances.domain.services.TransactionQueryService;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.RegisterTransactionCommandFromResourceAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.TransactionResourceFromEntityAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.RegisterTransactionResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionGroupByDateResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionResource;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.TransactionWithCategoryDesign;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionEntryId;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageWrapperResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TransactionsController is a REST controller that manages financial transactions.
 * It provides endpoints for retrieving transaction details and filtered transaction lists.
 */
@RestController
@RequestMapping(value = "api/v1/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Transactions", description = "Endpoints related to financial transactions management")
@Slf4j
public class TransactionsController {

    private final TransactionQueryService transactionQueryService;

    private final TransactionCommandService transactionCommandService;

    public TransactionsController(TransactionQueryService transactionQueryService, TransactionCommandService transactionCommandService) {
        this.transactionQueryService = transactionQueryService;
        this.transactionCommandService = transactionCommandService;
    }

    /**
     * Retrieves a transaction by its ID.
     *
     * @param id transaction identifier
     * @return transaction resource if found
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get transaction by ID",
            description = "Endpoint to retrieve a financial transaction by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found"
            )
    })
    public ResponseEntity<TransactionResource> getTransactionById(@PathVariable Long id) {

        var transactionId = new TransactionEntryId(id);

        var query = new GetTransactionByIdQuery(transactionId);

        var transaction = transactionQueryService.handle(query);

        if (transaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = TransactionResourceFromEntityAssembler
                .toResourceFromEntity(transaction.get());

        return ResponseEntity.ok(resource);
    }

    /**
     * Retrieves transactions using optional filters.
     * Available filters:
     * - ownerId
     * - transactionType
     * Transactions are grouped by creation date.
     * Examples:
     * - /api/v1/transactions?ownerId=1
     * - /api/v1/transactions?ownerId=1&transactionType=EXPENSE
     *
     * @param ownerId owner identifier
     * @param transactionType transaction type filter
     * @return grouped transactions
     */
    @GetMapping
    @Operation(
            summary = "Get transactions by owner or transaction type.",
            description = """
                    Endpoint to retrieve financial transactions using optional filters.
                    
                    Available filters:
                    - ownerId → Retrieves all transactions for a specific owner.
                    - transactionType → Filters transactions by type (INCOME or EXPENSE).
                    
                    Transactions are grouped by creation date.
                    
                    Examples:
                    - /api/v1/transactions?ownerId=1
                    - /api/v1/transactions?ownerId=1&transactionType=EXPENSE
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
            )
    })
    public ResponseEntity<MessageWrapperResponse<List<TransactionGroupByDateResource>>> getTransactions(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String transactionType
    ) {

        List<TransactionWithCategoryDesign> transactions;

        if (ownerId != null && transactionType != null) {

            var query = new GetTransactionsByOwnerIdAndTransactionTypeQuery(ownerId, transactionType);

            transactions = transactionQueryService.handle(query);

        } else if (ownerId != null) {

            var query = new GetTransactionsByOwnerIdQuery(ownerId);

            transactions = transactionQueryService.handle(query);

        } else {

            return ResponseEntity.badRequest().build();
        }
        var resources = TransactionResourceFromEntityAssembler
                .toGroupedResourcesFromEntities(transactions);

        if (transactions.isEmpty()) {

            return ResponseEntity.status(HttpStatus.OK).body(
                    new MessageWrapperResponse<>(
                            "No transactions found for the provided criteria.",
                            resources
                    )
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageWrapperResponse<>(
                        "Transactions retrieved successfully.",
                        resources
                )
        );
    }

    /**
     * Retrieves the last 5 transactions by owner ID.
     *
     * @param ownerId owner identifier
     * @return last 5 transactions
     */
    @GetMapping("/lastest")
    @Operation(
            summary = "Get last 5 transactions by owner ID",
            description = "Endpoint to retrieve the last 5 financial transactions for a specific owner."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
            )
    })
    public ResponseEntity<MessageWrapperResponse<List<TransactionGroupByDateResource>>> getLastTransactions(
            @RequestParam Long ownerId
    ) {
        var query = new GetLastTransactionsByOwnerIdQuery(ownerId);

        var transactions = transactionQueryService.handle(query);

        var resources = TransactionResourceFromEntityAssembler
                .toGroupedResourcesFromEntities(transactions);

        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new MessageWrapperResponse<>(
                            "No transactions found for the provided owner.",
                            resources
                    )
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageWrapperResponse<>(
                        "Last transactions retrieved successfully.",
                        resources
                )
        );
    }

    /**
     * Registers a new transaction using the owner user id from the request body.
     *
     * @param resource request payload describing the transaction
     * @return the created transaction or an error response
     */
    @PostMapping
    @Operation(
            summary = "Register a new individual financial transaction",
            description = """
                Endpoint to register a new financial transaction.

                This endpoint creates a new transaction associated with a user financial account.

                The transaction can be either:
                - INCOME
                - EXPENSE

                The system validates:
                - Financial account existence
                - Category existence
                - Currency code validity
                - Available balance for expenses
                - Owner type validity
                - Required Idempotency-Key header or clientOperationId body value for safe retries

                When transactionType is EXPENSE, the finances context also consumes active spending limits that match:
                - Same ownerId and ownerType
                - Same categoryId for CATEGORY limits
                - Same financialAccountId for FINANCIAL_ACCOUNT limits
                - Same currency and active period

                Repeating the same request with the same Idempotency-Key returns the previously registered transaction
                without applying balance or spending-limit effects again.

                If the request is valid, the transaction is stored successfully.
                """
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction registered successfully"
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid transaction data"
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Offline transaction conflicts with the current financial account state"
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error"
            )
    })
    public ResponseEntity<?> registerTransaction(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Transaction to register. EXPENSE transactions automatically consume matching spending limits.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterTransactionResource.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Personal expense",
                                            summary = "Consumes INDIVIDUAL limits matching categoryId and financialAccountId",
                                            value = """
                                                    {
                                                      "amount": 80.00,
                                                      "currencyCode": "PEN",
                                                      "description": "Cena",
                                                      "financialAccountId": 3,
                                                      "userId": 1,
                                                      "performedByUserId": 1,
                                                      "transactionType": "EXPENSE",
                                                      "categoryId": 5,
                                                      "ownerType": "INDIVIDUAL"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Family expense",
                                            summary = "Consumes FAMILY limits matching categoryId and financialAccountId",
                                            value = """
                                                    {
                                                      "amount": 250.00,
                                                      "currencyCode": "PEN",
                                                      "description": "Compra familiar de supermercado",
                                                      "financialAccountId": 12,
                                                      "userId": 2,
                                                      "performedByUserId": 4,
                                                      "transactionType": "EXPENSE",
                                                      "categoryId": 8,
                                                      "ownerType": "FAMILY"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody RegisterTransactionResource resource,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        try {
            if (resource.userId() == null) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResource("userId body value is required."));
            }

            if ((idempotencyKey == null || idempotencyKey.isBlank())
                    && (resource.clientOperationId() == null || resource.clientOperationId().isBlank())) {
                log.warn(
                        "Transaction registration rejected because idempotency key is missing. userId={}, financialAccountId={}",
                        resource.userId(),
                        resource.financialAccountId()
                );
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResource("Idempotency-Key header or clientOperationId body value is required."));
            }

            var idempotencySource = idempotencyKey == null || idempotencyKey.isBlank()
                    ? "clientOperationId"
                    : "Idempotency-Key";

            log.info(
                    "Transaction registration request received. userId={}, financialAccountId={}, idempotencySource={}, idempotencyKey={}",
                    resource.userId(),
                    resource.financialAccountId(),
                    idempotencySource,
                    idempotencyKey == null || idempotencyKey.isBlank() ? resource.clientOperationId() : idempotencyKey
            );

            var registerTransactionCommand = RegisterTransactionCommandFromResourceAssembler
                    .toCommandFromResource(resource, idempotencyKey);
            var transaction = transactionCommandService.handle(registerTransactionCommand);
            var transactionResource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transaction.get());
            return new ResponseEntity<>(transactionResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(new MessageResource(e.getMessage()));

        } catch (InsufficientFundsException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResource(e.getMessage()));

        } catch (FinancialAccountSyncConflictException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResource(e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResource(
                            "Unexpected server error."
                    ));
        }
    }
}

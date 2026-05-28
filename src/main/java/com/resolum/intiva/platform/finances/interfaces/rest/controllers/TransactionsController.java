package com.resolum.intiva.platform.finances.interfaces.rest.controllers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionByIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdAndTransactionTypeQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.services.TransactionQueryService;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.TransactionResourceFromEntityAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionGroupByDateResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionEntryId;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageWrapperResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TransactionsController is a REST controller that manages financial transactions.
 * It provides endpoints for retrieving transaction details and filtered transaction lists.
 */
@RestController
@RequestMapping(value = "api/v1/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Transactions", description = "Endpoints related to financial transactions management")
public class TransactionsController {

    private final TransactionQueryService transactionQueryService;

    public TransactionsController(TransactionQueryService transactionQueryService) {
        this.transactionQueryService = transactionQueryService;
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
     *
     * Available filters:
     * - ownerId
     * - transactionType
     *
     * Transactions are grouped by creation date.
     *
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
            summary = "Get transactions",
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

        List<Transaction> transactions;

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
}
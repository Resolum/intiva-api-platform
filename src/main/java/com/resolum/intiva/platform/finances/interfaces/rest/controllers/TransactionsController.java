package com.resolum.intiva.platform.finances.interfaces.rest.controllers;


import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionByIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdAndTransactionTypeQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.services.TransactionQueryService;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.TransactionResourceFromEntityAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionEntryId;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageWrapperResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TransactionsController is a REST controller that manages financial transactions. It provides endpoints for retrieving transaction details by ID. The controller interacts with the TransactionQueryService to handle transaction retrieval queries. It also includes error handling to return appropriate responses when a transaction with the provided ID is not found.
 */
@RestController
@RequestMapping(value = "api/v1/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Transactions", description = "Endpoints related to financial transactions management")
public class TransactionsController {

    /**
     * TransactionQueryService is a service that handles queries related to transactions, such as retrieving transaction details by ID. It is injected into the controller to perform the necessary business logic for transaction retrieval operations.
     */
    private final TransactionQueryService transactionQueryService;

    /**
     * Constructor for TransactionsController.
     *
     * @param transactionQueryService the TransactionQueryService to be used by this controller
     */
    public TransactionsController(TransactionQueryService transactionQueryService) {
        this.transactionQueryService = transactionQueryService;
    }


    /**
     * Endpoint to retrieve a financial transaction by its ID. It returns the transaction details if a transaction with the provided ID exists, or a 404 Not Found response if no such transaction is found.
     *
     * @param id The ID of the transaction to retrieve, provided as a path variable in the URL.
     * @return A ResponseEntity containing the TransactionResource if the transaction is found, or a 404 Not Found response if no transaction with the provided ID exists.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get transaction by ID",
            description = "Endpoint to retrieve a financial transaction by its ID. It returns the transaction details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found with the provided ID"
            )
    })
    public ResponseEntity<TransactionResource> getTransactionById(@PathVariable Long id) {
        var transactionId = new TransactionEntryId(id);
        var getTransactionByIdQuery = new GetTransactionByIdQuery(transactionId);
        var transaction = transactionQueryService.handle(getTransactionByIdQuery);
        if (transaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var transactionResource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transaction.get());
        return ResponseEntity.ok(transactionResource);
    }

    /**
     * Endpoint to retrieve financial transactions by owner ID and transaction type. It allows users to retrieve transactions based on the owner's unique identifier and the type of transaction (e.g., INCOME or EXPENSE). If both parameters are provided, it retrieves transactions that match both criteria. If only the owner ID is provided, it retrieves all transactions associated with that owner. If neither parameter is provided, it returns a 400 Bad Request response.
     *
     * @param ownerId         The ID of the owner whose transactions are to be retrieved, provided as a request parameter in the URL.
     * @param transactionType The type of transaction to filter by (e.g., INCOME or EXPENSE), provided as a request parameter in the URL.
     * @return A ResponseEntity containing a list of TransactionResource objects that match the specified criteria, or a 400 Bad Request response if neither parameter is provided.
     */
    @GetMapping
    @Operation(
            summary = "Get transactions by owner ID and transaction type",
            description = """
        Endpoint to retrieve financial transactions using optional filters.

        Available filters:
        - ownerId → Retrieves all transactions for a specific owner.
        - transactionType → Filters transactions by type (INCOME or EXPENSE).

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
    public ResponseEntity<MessageWrapperResponse<List<TransactionResource>>> getTransactionsByOwnerIdAndTransactionType(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String transactionType
    ) {
        List<Transaction> transactions;

        if (ownerId != null && transactionType != null) {

            var query = new GetTransactionsByOwnerIdAndTransactionTypeQuery(
                    ownerId,
                    switch (transactionType.toUpperCase()) {
                        case "INCOME" -> TransactionTypes.INCOME;
                        case "EXPENSE" -> TransactionTypes.EXPENSE;
                        default -> throw new IllegalArgumentException(
                                "Invalid transaction type: " + transactionType
                        );
                    }
            );

            transactions = transactionQueryService.handle(query);

        } else if (ownerId != null) {

            var query = new GetTransactionsByOwnerIdQuery(ownerId);

            transactions = transactionQueryService.handle(query);

        } else {

            return ResponseEntity.badRequest().build();
        }

        var resources = transactions.stream()
                .map(TransactionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        if(transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new MessageWrapperResponse<>("No transactions found for the provided criteria.", resources
            ));
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageWrapperResponse<>("Transactions retrieved successfully.", resources)
        );
    }
}

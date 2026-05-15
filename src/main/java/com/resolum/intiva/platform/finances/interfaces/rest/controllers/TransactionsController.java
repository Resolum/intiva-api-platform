package com.resolum.intiva.platform.finances.interfaces.rest.controllers;

import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionByIdQuery;
import com.resolum.intiva.platform.finances.domain.services.TransactionCommandService;
import com.resolum.intiva.platform.finances.domain.services.TransactionQueryService;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.RegisterTransactionCommandFromResourceAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.TransactionResourceFromEntityAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.RegisterTransactionResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionEntryId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TransactionsController is a REST controller that handles endpoints related to financial transactions. It defines the API endpoints for managing transactions, such as registering a new transaction and retrieving transaction details by ID. The controller uses the TransactionCommandService to perform business logic for transaction registration and the TransactionQueryService for transaction retrieval operations.
 */
@RestController
@RequestMapping(value = "/api/v1/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Transactions", description = "Endpoints for managing financial transactions")
public class TransactionsController {

    // TransactionCommandService is a service that handles commands related to transactions, such as registering a new transaction. It is injected into the controller to perform the necessary business logic for transaction registration operations.
    private final TransactionCommandService transactionCommandService;

    // TransactionQueryService is a service that handles queries related to transactions, such as retrieving transaction details by ID. It is injected into the controller to perform the necessary business logic for transaction retrieval operations.
    private final TransactionQueryService transactionQueryService;

    // Constructor injection for the TransactionCommandService and TransactionQueryService dependencies
    public TransactionsController(TransactionCommandService transactionCommandService, TransactionQueryService transactionQueryService) {
        this.transactionCommandService = transactionCommandService;
        this.transactionQueryService = transactionQueryService;
    }

    /**
     * Endpoint to register a new financial transaction. It accepts transaction details and creates a new transaction record if the provided information is valid. If the registration is successful, it returns a 201 Created response with the created TransactionResource. If the input data is invalid, it returns a 400 Bad Request response.
     * @param resource The RegisterTransactionResource object containing the transaction details sent in the request body (e.g., amount, description, date).
     * @return A ResponseEntity containing the created TransactionResource if the registration is successful, or an appropriate error response if the registration fails (e.g., due to invalid input data).
     */
    @PostMapping
    @Operation(
            summary = "Register a new financial transaction",
            description = "Endpoint to register a new financial transaction. It accepts transaction details and creates a new transaction record if the provided information is valid."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transaction registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data for transaction registration"
            )
    })
    public ResponseEntity<TransactionResource> registerTransaction(
            @RequestBody RegisterTransactionResource resource
    ) {
        var registerTransactionCommand = RegisterTransactionCommandFromResourceAssembler.toCommandFromResource(resource);
        var transaction = transactionCommandService.handle(registerTransactionCommand);
        if (transaction.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var transactionResource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transaction.get());
        return new ResponseEntity<>(transactionResource, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve a financial transaction by its ID. It returns the transaction details if a transaction with the provided ID exists, or a 404 Not Found response if no such transaction is found.
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
}

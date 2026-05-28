package com.resolum.intiva.platform.finances.interfaces.rest.controllers;

import com.resolum.intiva.platform.finances.domain.services.TransactionCommandService;
import com.resolum.intiva.platform.finances.domain.services.TransactionQueryService;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.RegisterTransactionCommandFromResourceAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.TransactionResourceFromEntityAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.RegisterTransactionResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UserTransactionsController is a REST controller that manages user transactions. It provides endpoints for registering new transactions and retrieving transaction details by ID. The controller interacts with the TransactionCommandService to handle transaction registration commands and the TransactionQueryService to handle transaction retrieval queries. It also includes error handling to return appropriate responses for invalid input data and unexpected server errors.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Endpoints related to user transactions management")
public class UserTransactionsController {

    // TransactionCommandService is a service that handles commands related to transactions, such as registering a new transaction. It is injected into the controller to perform the necessary business logic for transaction registration operations.
    private final TransactionCommandService transactionCommandService;

    // Constructor injection for the TransactionCommandService and TransactionQueryService dependencies
    public UserTransactionsController(TransactionCommandService transactionCommandService, TransactionQueryService transactionQueryService) {
        this.transactionCommandService = transactionCommandService;
    }

    /**
     * Endpoint to register a new financial transaction. It accepts transaction details and creates a new transaction record if the provided information is valid. If the registration is successful, it returns a 201 Created response with the created TransactionResource. If the input data is invalid, it returns a 400 Bad Request response.
     * @param resource The RegisterTransactionResource object containing the transaction details sent in the request body (e.g., amount, description, date).
     * @return A ResponseEntity containing the created TransactionResource if the registration is successful, or an appropriate error response if the registration fails (e.g., due to invalid input data).
     */
    @PostMapping("/{userId}/transactions")
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
                    responseCode = "500",
                    description = "Unexpected server error"
            )
    })
    public ResponseEntity<?> registerTransaction(
            @RequestBody RegisterTransactionResource resource,
            @PathVariable Long userId
    ) {
        try {
            var registerTransactionCommand = RegisterTransactionCommandFromResourceAssembler.toCommandFromResource(resource, userId);
            var transaction = transactionCommandService.handle(registerTransactionCommand);
            var transactionResource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transaction.get());
            return new ResponseEntity<>(transactionResource, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
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

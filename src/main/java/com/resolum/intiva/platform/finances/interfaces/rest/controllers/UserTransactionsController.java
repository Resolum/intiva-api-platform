package com.resolum.intiva.platform.finances.interfaces.rest.controllers;

import com.resolum.intiva.platform.categories.domain.model.exceptions.FinancialAccountSyncConflictException;
import com.resolum.intiva.platform.finances.domain.services.TransactionCommandService;
import com.resolum.intiva.platform.finances.domain.services.TransactionQueryService;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.RegisterTransactionCommandFromResourceAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.assemblers.TransactionResourceFromEntityAssembler;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.RegisterTransactionResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
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

/**
 * REST controller that registers user-scoped transactions.
 *
 * <p>Expense transactions registered here also trigger spending-limit consumption in the finances bounded context.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Endpoints related to user transactions management")
public class UserTransactionsController {

    /**
     * Command service used to register transactions.
     */
    private final TransactionCommandService transactionCommandService;

    /**
     * Creates the controller with the required transaction command service.
     *
     * @param transactionCommandService command service dependency
     * @param transactionQueryService query service dependency kept for compatibility with the existing constructor shape
     */
    public UserTransactionsController(TransactionCommandService transactionCommandService, TransactionQueryService transactionQueryService) {
        this.transactionCommandService = transactionCommandService;
    }

    /**
     * Registers a new transaction for the given user path id.
     *
     * @param resource request payload describing the transaction
     * @param userId owner identifier passed in the path
     * @return the created transaction or an error response
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

                When transactionType is EXPENSE, the finances context also consumes active spending limits that match:
                - Same ownerId and ownerType
                - Same categoryId for CATEGORY limits
                - Same financialAccountId for FINANCIAL_ACCOUNT limits
                - Same currency and active period
                
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

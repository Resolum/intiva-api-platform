package com.resolum.intiva.platform.categories.interfaces.rest.controllers;

import com.resolum.intiva.platform.categories.domain.model.queries.GetAllFinancialAccountsByOwnerId;
import com.resolum.intiva.platform.categories.domain.services.FinancialAccountQueryService;
import com.resolum.intiva.platform.categories.interfaces.rest.assemblers.FinancialAccountResourceFromEntityAssembler;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.responses.FinancialAccountResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserFinancialAccountsController is a REST controller that handles HTTP requests related to user financial accounts management.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Endpoints related to user financial accounts management")
public class UserFinancialAccountsController {

    /**
     * The FinancialAccountQueryService is a service that provides methods to query financial account data.
     */
    private final FinancialAccountQueryService financialAccountQueryService;

    /**
     * Constructor for UserFinancialAccountsController.
     * @param financialAccountQueryService the FinancialAccountQueryService to be used by this controller
     */
    public UserFinancialAccountsController(FinancialAccountQueryService financialAccountQueryService) {
        this.financialAccountQueryService = financialAccountQueryService;
    }

    /**
     * Retrieves all financial accounts associated with a user.
     * @param userId the ID of the user whose financial accounts are to be retrieved
     * @return a ResponseEntity containing a list of FinancialAccountResource objects representing the user's financial accounts
     */
    @GetMapping("/{userId}/financial-accounts")
    @Operation(summary = "Get All Financial Accounts by User ID", description = "Retrieves all financial accounts associated with a specific user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved financial accounts for the user.")
    public ResponseEntity<List<FinancialAccountResource>> getFinancialAccountsByUserId(@PathVariable Long userId) {
        var query = new GetAllFinancialAccountsByOwnerId(userId);
        var financialAccounts = financialAccountQueryService.handle(query);

        var financialAccountResources = financialAccounts.stream()
                .map(FinancialAccountResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(financialAccountResources);
    }
}

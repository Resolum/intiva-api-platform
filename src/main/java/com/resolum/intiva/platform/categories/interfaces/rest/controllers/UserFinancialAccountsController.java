package com.resolum.intiva.platform.categories.interfaces.rest.controllers;

import com.resolum.intiva.platform.categories.domain.model.commands.CreateFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.commands.UpdateFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.queries.GetAllFinancialAccountsByOwnerId;
import com.resolum.intiva.platform.categories.domain.model.queries.GetFinancialAccountByIdQuery;
import com.resolum.intiva.platform.categories.domain.services.FinancialAccountCommandService;
import com.resolum.intiva.platform.categories.domain.services.FinancialAccountQueryService;
import com.resolum.intiva.platform.categories.interfaces.rest.assemblers.FinancialAccountResourceFromEntityAssembler;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.requests.CreateFinancialAccountResource;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.requests.UpdateFinancialAccountResource;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.responses.FinancialAccountResource;
import com.resolum.intiva.platform.household.domain.exceptions.ResourceNotFoundException;
import com.resolum.intiva.platform.household.domain.exceptions.UnauthorizedException;
import com.resolum.intiva.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.resolum.intiva.platform.iam.domain.services.UserQueryService;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Endpoints related to user financial accounts management")
public class UserFinancialAccountsController {

    private final FinancialAccountQueryService financialAccountQueryService;
    private final FinancialAccountCommandService financialAccountCommandService;
    private final UserQueryService userQueryService;

    public UserFinancialAccountsController(
            FinancialAccountQueryService financialAccountQueryService,
            FinancialAccountCommandService financialAccountCommandService,
            UserQueryService userQueryService) {
        this.financialAccountQueryService = financialAccountQueryService;
        this.financialAccountCommandService = financialAccountCommandService;
        this.userQueryService = userQueryService;
    }

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

    @PostMapping("/{userId}/financial-accounts")
    @Operation(summary = "Create a Financial Account", description = "Creates a new financial account for the specified user.")
    @ApiResponse(responseCode = "201", description = "Financial account created successfully.")
    public ResponseEntity<FinancialAccountResource> createFinancialAccount(
            @PathVariable Long userId,
            @Valid @RequestBody CreateFinancialAccountResource resource) {

        userQueryService.handle(new GetUserByIdQuery(new UserId(userId)))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        var command = new CreateFinancialAccountCommand(
                resource.name(),
                resource.accountType(),
                resource.currencyCode() != null ? resource.currencyCode() : "PEN",
                resource.creditLimit(),
                resource.initialAmount(),
                resource.institution(),
                userId
        );

        var account = financialAccountCommandService.handle(command);
        var resourceResult = FinancialAccountResourceFromEntityAssembler.toResourceFromEntity(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceResult);
    }

    @PatchMapping("/{userId}/financial-accounts/{accountId}")
    @Operation(summary = "Update a Financial Account", description = "Partially updates a financial account. Only name and isActive can be modified.")
    @ApiResponse(responseCode = "200", description = "Financial account updated successfully.")
    public ResponseEntity<FinancialAccountResource> updateFinancialAccount(
            @PathVariable Long userId,
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateFinancialAccountResource resource) {

        if (resource.name() == null && resource.isActive() == null) {
            throw new IllegalArgumentException("At least one field (name, isActive) must be provided for update");
        }

        userQueryService.handle(new GetUserByIdQuery(new UserId(userId)))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        var account = financialAccountQueryService.handle(new GetFinancialAccountByIdQuery(accountId))
                .orElseThrow(() -> new ResourceNotFoundException("Financial account not found with ID: " + accountId));

        if (!account.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("You do not own this financial account");
        }

        var command = new UpdateFinancialAccountCommand(
                accountId,
                resource.name(),
                resource.isActive(),
                null
        );

        var updatedAccount = financialAccountCommandService.handle(command);
        var resourceResult = FinancialAccountResourceFromEntityAssembler.toResourceFromEntity(updatedAccount);
        return ResponseEntity.ok(resourceResult);
    }
}

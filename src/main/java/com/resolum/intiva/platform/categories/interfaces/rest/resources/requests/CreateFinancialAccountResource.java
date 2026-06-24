package com.resolum.intiva.platform.categories.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "CreateFinancialAccountResource", description = "Request to create a new financial account")
public record CreateFinancialAccountResource(
        @NotBlank(message = "Account name must not be blank")
        @Size(max = 100, message = "Account name must not exceed 100 characters")
        @Schema(description = "The name of the financial account", example = "Mi Billetera")
        String name,

        @NotBlank(message = "Account type must not be blank")
        @Schema(description = "The type of financial account (WALLET, CREDITCARD, DEBITCARD)", example = "WALLET")
        String accountType,

        @Schema(description = "The currency code (PEN, USD, EUR)", example = "PEN")
        String currencyCode,

        @NotNull(message = "Initial amount must not be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Initial amount must be >= 0")
        @Schema(description = "The initial amount in the account", example = "1000.00")
        BigDecimal initialAmount,

        @Schema(description = "The credit limit (required only for CREDITCARD)", example = "5000.00")
        BigDecimal creditLimit,

        @Schema(description = "The financial institution (required for CREDITCARD and DEBITCARD)", example = "BCP")
        String institution
) {}
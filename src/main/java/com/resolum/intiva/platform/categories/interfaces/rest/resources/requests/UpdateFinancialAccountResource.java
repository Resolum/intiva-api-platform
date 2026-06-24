package com.resolum.intiva.platform.categories.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "UpdateFinancialAccountResource", description = "Request to partially update a financial account")
public record UpdateFinancialAccountResource(
        @Size(max = 100, message = "Account name must not exceed 100 characters")
        @Schema(description = "The name of the financial account", example = "Mi Billetera Actualizada")
        String name,

        @Schema(description = "Whether the account is active", example = "true")
        Boolean isActive
) {}
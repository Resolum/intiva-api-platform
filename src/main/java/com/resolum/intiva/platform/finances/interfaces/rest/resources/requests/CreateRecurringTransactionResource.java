package com.resolum.intiva.platform.finances.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request body used to create a recurring transaction definition.
 *
 * @param amount amount that each generated transaction will use
 * @param currencyCode currency code used by every generated transaction
 * @param description description copied into generated transactions
 * @param ownerId owner of the recurring definition
 * @param financialAccountId financial account referenced by generated transactions
 * @param performedByUserId user recorded as the actor when generated transactions are created
 * @param transactionType type of generated transactions
 * @param categoryId category assigned to generated transactions
 * @param ownerType owner scope, personal or family
 * @param frequency cadence used to generate future transactions
 * @param startDate first eligible execution date
 * @param endDate optional inclusive final execution date
 */
@Schema(description = "Request body used to create a recurring income or expense definition.")
public record CreateRecurringTransactionResource(
        @Schema(description = "Amount used by every generated transaction.", example = "1500.00")
        BigDecimal amount,

        @Schema(description = "Currency code used by every generated transaction.", example = "PEN", allowableValues = {"PEN", "USD", "EUR"})
        String currencyCode,

        @Schema(description = "Human-readable description for generated transactions.", example = "Sueldo")
        String description,

        @Schema(description = "Owner identifier. For INDIVIDUAL this is a user id, for FAMILY this is a family id.", example = "1")
        Long ownerId,

        @Schema(description = "Financial account that generated transactions affect.", example = "3")
        Long financialAccountId,

        @Schema(description = "User recorded as the actor of the generated transactions.", example = "1")
        Long performedByUserId,

        @Schema(description = "Type of generated transactions.", example = "INCOME", allowableValues = {"INCOME", "EXPENSE"})
        String transactionType,

        @Schema(description = "Category assigned to generated transactions.", example = "5")
        Long categoryId,

        @Schema(description = "Owner scope. INDIVIDUAL is personal finance and FAMILY is group finance.", example = "INDIVIDUAL", allowableValues = {"INDIVIDUAL", "FAMILY"})
        String ownerType,

        @Schema(description = "Recurring cadence used to calculate the next execution date.", example = "MONTHLY", allowableValues = {"WEEKLY", "BIWEEKLY", "MONTHLY"})
        String frequency,

        @Schema(description = "First date on which the definition should execute.", example = "2026-06-15")
        LocalDate startDate,

        @Schema(description = "Optional last date on which the definition can still execute.", example = "2026-12-15", nullable = true)
        LocalDate endDate
) {
}

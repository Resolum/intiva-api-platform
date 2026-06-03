package com.resolum.intiva.platform.finances.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * RegisterTransactionResource is a record that represents the data structure of a financial transaction as it is received through the REST API when registering a new transaction. It contains fields that correspond to the properties of a Transaction entity, including the amount, currency code, description, owner ID, financial account ID, actor user ID, transaction type, and category ID.
 * @param amount The amount of the transaction, represented as a string to maintain precision and avoid issues with floating-point representation. It should be a valid decimal number in string format.
 * @param currencyCode The ISO 4217 currency code associated with the transaction, such as "USD" for US dollars or "EUR" for euros. This field is mandatory and must be a valid currency code.
 * @param description A brief description of the transaction, providing context and details about the nature of the transaction. This field is mandatory and cannot be null or blank.
 * @param financialAccountId The identifier of the financial account associated with the transaction, represented as a long value. This field is mandatory and must correspond to an existing financial account in the system.
 * @param performedByUserId The identifier of the user who performed the transaction, represented as a string. This field is mandatory and must not be null or blank, as it is important for tracking the user responsible for the transaction.
 * @param transactionType The type of the transaction, indicating whether it is an income or an expense. This field is mandatory and must be a valid value corresponding to the TransactionTypes enum, such as "INCOME" or "EXPENSE".
 * @param categoryId The identifier of the category associated with the transaction, represented as a long value. This field is optional and can be null if the transaction does not belong to any category. If provided, it must correspond to an existing category in the system.
 */
@Schema(description = "Request body used to register an income or expense transaction.")
public record RegisterTransactionResource(
        @Schema(description = "Transaction amount. It must be greater than zero.", example = "80.00")
        BigDecimal amount,

        @Schema(description = "Currency code used by the transaction.", example = "PEN", allowableValues = {"PEN", "USD", "EUR"})
        String currencyCode,

        @Schema(description = "Human-readable transaction description.", example = "Cena")
        String description,

        @Schema(description = "Financial account/payment method used by the transaction.", example = "3")
        Long financialAccountId,

        @Schema(description = "User who performed the transaction. In family transactions this can differ from ownerId.", example = "1")
        Long performedByUserId,

        @Schema(description = "Transaction type. Spending limits are consumed only by EXPENSE transactions.", example = "EXPENSE", allowableValues = {"INCOME", "EXPENSE"})
        String transactionType,

        @Schema(description = "Category assigned to the transaction.", example = "5")
        Long categoryId,

        @Schema(description = "Owner scope. INDIVIDUAL is personal finance and FAMILY is group/family finance.", example = "INDIVIDUAL", allowableValues = {"INDIVIDUAL", "FAMILY"})
        String ownerType
) {
}

package com.resolum.intiva.platform.finances.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload that exposes a recurring transaction definition through the REST API.
 *
 * @param id recurring transaction identifier
 * @param amount amount used by generated transactions
 * @param currencyCode currency used by generated transactions
 * @param description description copied into generated transactions
 * @param ownerId owner that owns the definition
 * @param ownerType ownership scope of the definition
 * @param financialAccountId financial account referenced by generated transactions
 * @param performedByUserId user recorded as actor when generated transactions are created
 * @param transactionType type of generated transactions
 * @param categoryId category assigned to generated transactions
 * @param frequency recurring cadence
 * @param startDate first eligible execution date
 * @param nextExecutionDate next scheduled execution date
 * @param lastExecutionDate most recent scheduled execution date that completed successfully
 * @param endDate optional inclusive final execution date
 * @param active whether the definition is currently active
 * @param createdAt timestamp at which the definition was created
 */
@Schema(description = "Recurring transaction definition returned by the finances API.")
public record RecurringTransactionResource(
        Long id,
        String amount,
        String currencyCode,
        String description,
        Long ownerId,
        String ownerType,
        Long financialAccountId,
        Long performedByUserId,
        String transactionType,
        Long categoryId,
        String frequency,
        String startDate,
        String nextExecutionDate,
        String lastExecutionDate,
        String endDate,
        Boolean active,
        String createdAt
) {
}

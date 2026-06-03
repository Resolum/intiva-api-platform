package com.resolum.intiva.platform.finances.domain.model.commands;

import com.resolum.intiva.platform.finances.domain.model.valueobjects.RecurringFrequency;
import com.resolum.intiva.platform.shared.domain.valueobjects.*;
import jakarta.validation.Valid;

import java.time.LocalDate;

/**
 * Command used to create a recurring transaction definition inside the finances bounded context.
 *
 * @param amount amount that will be used every time the recurring definition materializes into a transaction
 * @param description business description copied into generated transactions
 * @param ownerId owner that owns the recurring definition
 * @param financialAccountId financial account that generated transactions will affect
 * @param performedByUserId user recorded as the actor when generated transactions are created
 * @param transactionType type of generated transactions
 * @param categoryId category assigned to generated transactions
 * @param ownerType owner scope for personal or family finances
 * @param frequency cadence used to calculate the next execution date
 * @param startDate first date on which the definition becomes eligible to run
 * @param endDate optional inclusive last date on which the definition may run
 */
public record CreateRecurringTransactionCommand(
        @Valid Money amount,
        String description,
        Long ownerId,
        @Valid FinancialAccountId financialAccountId,
        @Valid UserId performedByUserId,
        TransactionTypes transactionType,
        @Valid CategoryId categoryId,
        OwnerTypes ownerType,
        RecurringFrequency frequency,
        LocalDate startDate,
        LocalDate endDate
) {
}

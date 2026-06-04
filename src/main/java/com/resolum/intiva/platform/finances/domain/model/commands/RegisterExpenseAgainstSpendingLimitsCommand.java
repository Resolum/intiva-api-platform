package com.resolum.intiva.platform.finances.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import jakarta.validation.Valid;

import java.time.LocalDate;

/**
 * Internal command used after an EXPENSE transaction is registered.
 *
 * <p>This command is not exposed directly through REST. It coordinates the transaction aggregate with the
 * SpendingLimit aggregate by carrying the transaction data needed to find and consume applicable limits.</p>
 *
 * @param transactionId transaction that originated the consumption
 * @param ownerId transaction owner id
 * @param ownerType transaction owner type
 * @param categoryId category used by the expense
 * @param financialAccountId financial account/payment method used by the expense
 * @param amount expense amount
 * @param transactionDate date used to evaluate active limit periods
 */
public record RegisterExpenseAgainstSpendingLimitsCommand(
        Long transactionId,
        Long ownerId,
        OwnerTypes ownerType,
        Long categoryId,
        Long financialAccountId,
        @Valid Money amount,
        LocalDate transactionDate
) {
}

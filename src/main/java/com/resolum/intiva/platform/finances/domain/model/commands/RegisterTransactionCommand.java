package com.resolum.intiva.platform.finances.domain.model.commands;

import com.resolum.intiva.platform.finances.domain.model.valueobjects.TransactionTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.CategoryId;
import com.resolum.intiva.platform.shared.domain.valueobjects.FinancialAccountId;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import jakarta.validation.Valid;

/**
 * RegisterTransactionCommand is a command object used to encapsulate the data required to register a new financial transaction in the system. It contains all the necessary information such as the amount, description, owner ID, financial account ID, actor user ID, transaction type, and category ID.
 * @param amount The monetary amount of the transaction, represented as a Money value object. This field is mandatory and must be a valid Money instance with a positive value.
 * @param description A brief description of the transaction. This field is mandatory and must not be null or blank.
 * @param ownerId The identifier of the owner of the transaction, represented as a String. This field is mandatory and must not be null or blank.
 * @param financialAccountId The identifier of the financial account associated with the transaction, represented as a FinancialAccountId value object. This field is mandatory and must be a valid FinancialAccountId instance.
 * @param actorUserId The identifier of the user who is performing the transaction, represented as a UserId value object. This field is mandatory and must be a valid UserId instance.
 * @param transactionType The type of the transaction, indicating whether it is an income or an expense. This field is mandatory and must be a valid TransactionTypes enum value.
 * @param categoryId The identifier of the category associated with the transaction, represented as a CategoryId value object. This field is optional and can be null if the transaction does not belong to any category.
 */
public record RegisterTransactionCommand(
        @Valid Money amount,
        String description,
        String ownerId,
        @Valid FinancialAccountId financialAccountId,
        @Valid UserId actorUserId,
        TransactionTypes transactionType,
        @Valid CategoryId categoryId) {
}

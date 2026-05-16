package com.resolum.intiva.platform.finances.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionEntryId;

/**
 * UpdateTransactionAmountCommand is a command object used to encapsulate the data required to update the amount of an existing financial transaction in the system. It contains the identifier of the transaction to be updated and the new amount to be set for that transaction.
 * @param transactionId The identifier of the transaction whose amount is to be updated, represented as a TransactionEntryId value object. This field is mandatory and must be a valid TransactionEntryId instance corresponding to an existing transaction in the system.
 * @param newAmount The new monetary amount to be set for the transaction, represented as a Money value object. This field is mandatory and must be a valid Money instance with a positive value. The new amount will replace the existing amount of the transaction when the command is executed.
 */
public record UpdateTransactionAmountCommand(
        TransactionEntryId transactionId,
        Money newAmount
) {
}

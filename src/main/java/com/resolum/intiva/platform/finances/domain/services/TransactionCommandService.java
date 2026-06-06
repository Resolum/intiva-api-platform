package com.resolum.intiva.platform.finances.domain.services;

import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.domain.model.commands.RegisterTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdateTransactionAmountCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdateTransactionDescriptionCommand;

import java.util.Optional;

/**
 * Service interface for handling transaction-related commands, such as registering a new transaction or updating existing transaction details.
 * This service is responsible for processing commands that modify transaction data, ensuring that business rules and validations are applied correctly.
 */
public interface TransactionCommandService {

    /**
     * Handles the command to register a new transaction.
     * @param command The command containing transaction registration details, such as amount, description, date, and associated account information.
     * @return An Optional containing the created Transaction if successful, or empty if the operation failed (e.g., due to validation errors or issues with the associated account).
     */
    Optional<Transaction> handle(RegisterTransactionCommand command);

    /**
     * Handles the command to update the description of an existing transaction.
     * @param command The command containing the transaction ID and the new description to be updated.
     * @return An Optional containing the updated Transaction if successful, or empty if the operation failed (e.g., due to validation errors or if the transaction does not exist).
     */
    Optional<Transaction> handle(UpdateTransactionDescriptionCommand command);

    /**
     * Handles the command to update the amount of an existing transaction.
     * @param command The command containing the transaction ID and the new amount to be updated.
     * @return An Optional containing the updated Transaction if successful, or empty if the operation failed (e.g., due to validation errors, issues with the associated account, or if the transaction does not exist).
     */
    Optional<Transaction> handle(UpdateTransactionAmountCommand command);
}

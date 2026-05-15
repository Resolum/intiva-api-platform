package com.resolum.intiva.platform.finances.application.internal.commandhandlers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.domain.model.commands.RegisterTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdateTransactionAmountCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdateTransactionDescriptionCommand;
import com.resolum.intiva.platform.finances.domain.services.TransactionCommandService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the TransactionCommandService interface that handles commands related to transactions, such as registering a new transaction and updating existing transaction details. This service is responsible for processing the commands, performing necessary validations, and interacting with the domain model to execute the requested operations.
 * The methods return an Optional containing the resulting Transaction if successful, or empty if the operation fails due to validation errors or other issues.
 */
@Service
public class TransactionCommandServiceImpl implements TransactionCommandService {

    // Logger for logging important events and errors in the transaction command service
    private final Logger LOGGER = LoggerFactory.getLogger(TransactionCommandServiceImpl.class);

    // TransactionRepository is used to interact with the database for transaction-related operations
    private final TransactionRepository transactionRepository;

    // Constructor injection for dependencies
    public TransactionCommandServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Handles the command to register a new transaction.
     *
     * @param command The command containing transaction registration details, such as amount, description, date, and associated account information.
     * @return An Optional containing the created Transaction if successful, or empty if the operation failed (e.g., due to validation errors or issues with the associated account).
     */
    @Override
    public Optional<Transaction> handle(RegisterTransactionCommand command) {
        try {
            var transaction = new Transaction(command);
            transactionRepository.save(transaction);
            return Optional.of(transaction);
        } catch (Exception e) {
            LOGGER.error("Error while registering transaction for user {}: {}", command.actorUserId(),e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Handles the command to update the description of an existing transaction.
     *
     * @param command The command containing the transaction ID and the new description to be updated.
     * @return An Optional containing the updated Transaction if successful, or empty if the operation failed (e.g., due to validation errors or if the transaction does not exist).
     */
    @Override
    public Optional<Transaction> handle(UpdateTransactionDescriptionCommand command) {
        try {
            var transaction = transactionRepository.findById(command.transactionId().transactionId())
                    .orElseThrow(() -> new IllegalArgumentException("Transaction with ID " + command.transactionId().transactionId() + " does not exist."));
            transaction.editDescription(command.description());
            transactionRepository.save(transaction);
            return Optional.of(transaction);
        } catch (Exception e) {
            LOGGER.error("Error while updating description for transaction {}: {}", command.transactionId(),e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Handles the command to update the amount of an existing transaction.
     *
     * @param command The command containing the transaction ID and the new amount to be updated.
     * @return An Optional containing the updated Transaction if successful, or empty if the operation failed (e.g., due to validation errors, issues with the associated account, or if the transaction does not exist).
     */
    @Override
    public Optional<Transaction> handle(UpdateTransactionAmountCommand command) {
        try {
            var transaction = transactionRepository.findById(command.transactionId().transactionId())
                    .orElseThrow(() -> new IllegalArgumentException("Transaction with ID " + command.transactionId().transactionId() + " does not exist."));
            transaction.editAmount(command.newAmount());
            transactionRepository.save(transaction);
            return Optional.of(transaction);
        } catch (Exception e) {
            LOGGER.error("Error while updating amount for transaction {}: {}", command.transactionId(),e.getMessage());
            return Optional.empty();
        }
    }
}

package com.resolum.intiva.platform.finances.application.internal.commandhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.domain.model.commands.RegisterExpenseAgainstSpendingLimitsCommand;
import com.resolum.intiva.platform.finances.domain.model.events.FamilyTransactionCreatedEvent;
import com.resolum.intiva.platform.finances.domain.model.events.RegisteredTransactionDetectedEvent;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalCategoriesService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.domain.model.commands.RegisterTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdateTransactionAmountCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdateTransactionDescriptionCommand;
import com.resolum.intiva.platform.finances.domain.services.SpendingLimitCommandService;
import com.resolum.intiva.platform.finances.domain.services.TransactionCommandService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.TransactionRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    // FinancesExternalCategoriesService is used to interact with the external categories service for checking if a category exists
    private final FinancesExternalCategoriesService financesExternalCategoriesService;

    // FinancesExternalFinancialAccountService is used to interact with the external financial account service for checking if a financial account exists
    private final FinancesExternalFinancialAccountService financesExternalFinancialAccountService;

        // ApplicationEventPublisher is used to publish domain events related to transaction operations
        private final ApplicationEventPublisher eventPublisher;

        /**
         * Service used to apply expense transactions to matching spending limits.
         */
        private final SpendingLimitCommandService spendingLimitCommandService;

    // Constructor injection for dependencies
    public TransactionCommandServiceImpl(TransactionRepository transactionRepository, FinancesExternalCategoriesService financesExternalCategoriesService, FinancesExternalFinancialAccountService financesExternalFinancialAccountService, ApplicationEventPublisher eventPublisher, SpendingLimitCommandService spendingLimitCommandService) {
        this.transactionRepository = transactionRepository;
        this.financesExternalCategoriesService = financesExternalCategoriesService;
        this.financesExternalFinancialAccountService = financesExternalFinancialAccountService;
        this.eventPublisher = eventPublisher;
        this.spendingLimitCommandService = spendingLimitCommandService;
    }

    /**
     * Handles the command to register a new transaction.
     *
     * @param command The command containing transaction registration details, such as amount, description, date, and associated account information.
     * @return An Optional containing the created Transaction if successful, or empty if the operation failed (e.g., due to validation errors or issues with the associated account).
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<Transaction> handle(RegisterTransactionCommand command) {
        try {

            if (command.clientOperationId() != null && !command.clientOperationId().isBlank()) {
                var existingTransaction = transactionRepository.findByClientOperationId(command.clientOperationId());
                if (existingTransaction.isPresent()) {
                    return existingTransaction;
                }
            }

            if (!financesExternalCategoriesService.existsCategoryById(command.categoryId().getValue())) {
                throw new IllegalArgumentException("Category with ID " + command.categoryId().getValue() + " does not exist.");

            }

            if (!financesExternalFinancialAccountService.existsFinancialAccountById(command.financialAccountId().getValue())) {
                throw new IllegalArgumentException("Financial account with ID " + command.financialAccountId().getValue() + " does not exist.");
            }

            eventPublisher.publishEvent(new RegisteredTransactionDetectedEvent(
                    this,
                    command.financialAccountId().getValue(),
                    command.ownerId(),
                    command.ownerTypes(),
                    command.categoryId(),
                    command.transactionType().name(),
                    command.amount().getAmount(),
                    command.amount().getCurrencyCode(),
                    command.baseAccountVersion()
            ));

            var transaction = new Transaction(command);

            var savedTransaction = transactionRepository.save(transaction);

            if (command.ownerTypes() == OwnerTypes.FAMILY) {
                eventPublisher.publishEvent(new FamilyTransactionCreatedEvent(
                        this,
                        command.ownerId(),
                        savedTransaction.getId(),
                        command.amount().getAmount(),
                        command.description(),
                        command.performedByUserId().getValue()
                ));
            }

            if (command.transactionType() == TransactionTypes.EXPENSE) {
                spendingLimitCommandService.handle(new RegisterExpenseAgainstSpendingLimitsCommand(
                        savedTransaction.getId(),
                        command.ownerId(),
                        command.ownerTypes(),
                        command.categoryId().getValue(),
                        command.financialAccountId().getValue(),
                        command.amount(),
                        LocalDate.now()
                ));
            }

            return Optional.of(savedTransaction);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while registering transaction: " + e.getMessage());
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
            LOGGER.error("Error while updating description for transaction {}: {}", command.transactionId(), e.getMessage());
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
            LOGGER.error("Error while updating amount for transaction {}: {}", command.transactionId(), e.getMessage());
            return Optional.empty();
        }
    }
}

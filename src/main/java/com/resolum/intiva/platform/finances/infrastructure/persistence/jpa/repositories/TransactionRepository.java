package com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Transaction entities in the database.
 * This interface extends JpaRepository, providing CRUD operations and custom query methods for Transaction entities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds a transaction list by its owner ID.
     * @param ownerId The ID of the owner of the transaction.
     * @return An Optional containing the Transaction if found, or empty if not found.
     */
    List<Transaction> findTransactionByOwnerId(String ownerId);

    /**
     * Finds a transaction list by its owner ID and transaction type.
     * @param ownerId The ID of the owner of the transaction.
     * @param transactionType The type of the transaction (e.g., EXPENSE, INCOME).
     * @return An Optional containing the Transaction if found, or empty if not found.
     */
    List<Transaction> findTransactionByOwnerIdAndTransactionType(String ownerId, TransactionTypes transactionType);
}

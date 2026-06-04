package com.resolum.intiva.platform.finances.domain.services;

import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.domain.model.queries.GetLastTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionByIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdAndTransactionTypeQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.TransactionWithCategoryDesign;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling transaction-related queries, such as retrieving transactions by ID or by owner ID.
 * This service is responsible for processing queries that fetch transaction data, ensuring that the appropriate data retrieval logic is applied based on the query parameters.
 */
public interface TransactionQueryService {

    /**
     * Handles the query to retrieve a transaction by its unique identifier.
     * @param query The query containing the transaction ID to be retrieved.
     * @return An Optional containing the Transaction if found, or empty if no transaction with the specified ID exists.
     */
    Optional<Transaction> handle(GetTransactionByIdQuery query);

    /**
     * Handles the query to retrieve transactions by the owner's unique identifier.
     * @param query The query containing the owner ID for which transactions are to be retrieved.
     * @return A list of Transactions associated with the specified owner ID. If no transactions are found, an empty list is returned.
     */
    List<TransactionWithCategoryDesign> handle(GetTransactionsByOwnerIdQuery query);

    /**
     * Handles the query to retrieve transactions by the owner's unique identifier and transaction type.
     * @param query The query containing the owner ID and transaction type for which transactions are to be retrieved.
     * @return A list of Transactions associated with the specified owner ID and transaction type. If no transactions are found, an empty list is returned.
     */
    List<TransactionWithCategoryDesign> handle(GetTransactionsByOwnerIdAndTransactionTypeQuery query);

    /**
     * Handles the query to retrieve the last transactions by the owner's unique identifier.
     * @param query The query containing the owner ID for which the last transactions are to be retrieved.
     * @return A list of the last Transactions associated with the specified owner ID. If no transactions are found, an empty list is returned.
     */
    List<TransactionWithCategoryDesign> handle(GetLastTransactionsByOwnerIdQuery query);
}

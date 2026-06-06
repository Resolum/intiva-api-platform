package com.resolum.intiva.platform.finances.application.internal.queryhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalCategoriesService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.domain.model.queries.GetLastTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionByIdQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdAndTransactionTypeQuery;
import com.resolum.intiva.platform.finances.domain.model.queries.GetTransactionsByOwnerIdQuery;
import com.resolum.intiva.platform.finances.domain.services.TransactionQueryService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.TransactionRepository;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.TransactionWithCategoryDesign;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the TransactionQueryService interface that handles queries related to transactions.
 * This service interacts with the TransactionRepository to retrieve transaction data from the database.
 */
@Service
public class TransactionQueryServiceImpl implements TransactionQueryService {

    // Repository for accessing transaction data from the database
    private final TransactionRepository transactionRepository;

    private final FinancesExternalCategoriesService financesExternalCategoriesService;

    // Constructor for dependency injection of the TransactionRepository
    public TransactionQueryServiceImpl(TransactionRepository transactionRepository, FinancesExternalCategoriesService financesExternalCategoriesService) {
        this.transactionRepository = transactionRepository;
        this.financesExternalCategoriesService = financesExternalCategoriesService;
    }

    /**
     * Handles the query to retrieve a transaction by its unique identifier.
     *
     * @param query The query containing the transaction ID to be retrieved.
     * @return An Optional containing the Transaction if found, or empty if no transaction with the specified ID exists.
     */
    @Override
    public Optional<Transaction> handle(GetTransactionByIdQuery query) {
        return transactionRepository.findById(query.transactionId().transactionId());
    }

    /**
     * Handles the query to retrieve transactions by the owner's unique identifier.
     *
     * @param query The query containing the owner ID for which transactions are to be retrieved.
     * @return A list of Transactions associated with the specified owner ID. If no transactions are found, an empty list is returned.
     */
    @Override
    public List<TransactionWithCategoryDesign> handle(GetTransactionsByOwnerIdQuery query) {
        List<Transaction> transactions = transactionRepository
                .findTransactionByOwnerId(query.ownerId());

        return transactions.stream().map(transaction -> {
            var designPair = financesExternalCategoriesService.getCategoryColorAndNameById(transaction.getCategoryId().getValue());

            return new TransactionWithCategoryDesign(transaction, designPair.getLeft(), designPair.getRight());
        }).collect(Collectors.toList());
    }

    /**
     * Handles the query to retrieve transactions by the owner's unique identifier and transaction type.
     *
     * @param query The query containing the owner ID and transaction type for which transactions are to be retrieved.
     * @return A list of Transactions associated with the specified owner ID and transaction type. If no transactions are found, an empty list is returned.
     */
    @Override
    public List<TransactionWithCategoryDesign> handle(GetTransactionsByOwnerIdAndTransactionTypeQuery query) {
        List<Transaction> transactions = transactionRepository
                .findTransactionByOwnerIdAndTransactionType(query.ownerId(), query.transactionType());

        return transactions.stream().map(transaction -> {
            var designPair = financesExternalCategoriesService.getCategoryColorAndNameById(transaction.getCategoryId().getValue());

            return new TransactionWithCategoryDesign(transaction, designPair.getLeft(), designPair.getRight());
        }).collect(Collectors.toList());
    }

    @Override
    public List<TransactionWithCategoryDesign> handle(GetLastTransactionsByOwnerIdQuery query) {
        List<Transaction> transactions = transactionRepository
                .findTop5ByOwnerIdOrderByCreatedAtDesc(query.ownerId());

        return transactions.stream().map(transaction -> {
            var designPair = financesExternalCategoriesService
                    .getCategoryColorAndNameById(transaction.getCategoryId().getValue());

            return new TransactionWithCategoryDesign(transaction, designPair.getLeft(), designPair.getRight());
        }).collect(Collectors.toList());
    }
}

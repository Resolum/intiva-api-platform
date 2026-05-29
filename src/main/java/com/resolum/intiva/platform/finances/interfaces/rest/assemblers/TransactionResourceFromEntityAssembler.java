package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.CategoryDesignResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionGroupByDateResource;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionResource;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.TransactionWithCategoryDesign;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionWithCategoryDesignResource;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TransactionResourceFromEntityAssembler is a utility class that provides a method to convert a Transaction entity from the domain model into a TransactionResource, which is a data transfer object used for REST API responses. This assembler helps to separate the concerns of the domain model and the API representation, allowing for a clean and maintainable codebase. By using this assembler, we can ensure that the data returned in API responses is properly formatted and contains all the necessary information extracted from the Transaction entity.
 */
public class TransactionResourceFromEntityAssembler {

    /**
     * Converts a Transaction entity along with its associated category information into a TransactionResource. This method takes an ImmutablePair containing the Transaction and another ImmutablePair with the category name and color, and constructs a TransactionResource that can be used in API responses.
     *
     * @param entity An ImmutablePair where the left element is a Transaction entity and the right element is another ImmutablePair containing the category name and color. The Transaction entity contains all the details of the financial transaction, while the category information provides additional context about the transaction's classification.
     * @return A TransactionResource object that represents the data of the Transaction entity in a format suitable for API responses.
     */
    public static TransactionWithCategoryDesignResource toResourceFromEntity(TransactionWithCategoryDesign entity) {

        return new TransactionWithCategoryDesignResource(
                entity.transaction().getId(),
                entity.transaction().getAmount().amount().toString(),
                entity.transaction().getAmount().getCurrencyCode(),
                entity.transaction().getDescription(),
                entity.transaction().getOwnerId(),
                entity.transaction().getFinancialAccountId().getValue(),
                entity.transaction().getPerformedByUserId().getValue(),
                entity.transaction().getTransactionType().name(),
                entity.transaction().getCategoryId().getValue(),
                new CategoryDesignResource(
                        entity.categoryColor(),
                        entity.categoryIcon()
                )
        );
    }

    /**
     * Converts a Transaction entity into a TransactionResource. This method takes a Transaction entity and constructs a TransactionResource that can be used in API responses. The TransactionResource includes all the relevant details of the transaction, such as the amount, currency, description, owner ID, financial account ID, user ID of the performer, transaction type, and category ID.
     *
     * @param entity A Transaction entity that contains all the details of a financial transaction. This entity is typically retrieved from the database and represents the domain model of a transaction.
     * @return A TransactionResource object that represents the data of the Transaction entity in a format suitable for API responses. The TransactionResource includes fields that are directly mapped from the Transaction entity.
     */
    public static TransactionResource toResourceFromEntity(Transaction entity) {
        return new TransactionResource(
                entity.getId(),
                entity.getAmount().amount().toString(),
                entity.getAmount().getCurrencyCode(),
                entity.getDescription(),
                entity.getOwnerId(),
                entity.getFinancialAccountId().getValue(),
                entity.getPerformedByUserId().getValue(),
                entity.getTransactionType().name(),
                entity.getCategoryId().getValue()
        );
    }

    /**
     * Converts a list of TransactionWithCategoryDesign entities into a list of TransactionGroupByDateResource objects, grouping the transactions by their creation date. This method uses Java Streams to group the transactions by the date they were created, and then maps each group to a TransactionGroupByDateResource that contains the date and the list of TransactionResource objects for that date.
     *
     * @param transactions A list of TransactionWithCategoryDesign entities that need to be converted and grouped by their creation date. Each entity contains a Transaction and its associated category information.
     * @return A list of TransactionGroupByDateResource objects, where each object represents a group of transactions that share the same creation date, along with the corresponding TransactionResource objects for those transactions.
     */
    public static List<TransactionGroupByDateResource> toGroupedResourcesFromEntities(List<TransactionWithCategoryDesign> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.transaction().getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate(),
                        Collectors.mapping(
                                TransactionResourceFromEntityAssembler::toResourceFromEntity,
                                Collectors.toList()
                        )
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey(java.util.Comparator.reverseOrder()))
                .map(e -> new TransactionGroupByDateResource(e.getKey(), e.getValue()))
                .toList();
    }
}

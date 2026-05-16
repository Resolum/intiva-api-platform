package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.responses.TransactionResource;

/**
 * TransactionResourceFromEntityAssembler is a utility class that provides a method to convert a Transaction entity from the domain model into a TransactionResource, which is a data transfer object used for REST API responses. This assembler helps to separate the concerns of the domain model and the API representation, allowing for a clean and maintainable codebase. By using this assembler, we can ensure that the data returned in API responses is properly formatted and contains all the necessary information extracted from the Transaction entity.
 */
public class TransactionResourceFromEntityAssembler {

    /**
     * Converts a Transaction entity into a TransactionResource, which is a data transfer object used for REST API responses. This method extracts the relevant information from the Transaction entity, such as the amount, currency code, description, owner ID, financial account ID, actor user ID, transaction type, category ID, and the date and time when the transaction was registered. It then constructs a new TransactionResource instance with this information, which can be returned in API responses to provide clients with a structured representation of the transaction data.
     * @param entity The Transaction entity that contains the data to be converted into a TransactionResource. This entity is expected to have all the necessary information about the transaction, including its properties and relationships with other entities in the domain model.
     * @return A TransactionResource instance that represents the data of the given Transaction entity in a format suitable for REST API responses. The returned TransactionResource will contain all the relevant information extracted from the Transaction entity, allowing clients to easily consume and understand the transaction data when it is returned in API responses.
     */
    public static TransactionResource toResourceFromEntity(Transaction entity) {
        return new TransactionResource(
                entity.getId(),
                entity.getAmount().amount().toString(),
                entity.getAmount().getCurrencyCode(),
                entity.getDescription(),
                entity.getOwnerId(),
                entity.getFinancialAccountId().getValue(),
                entity.getActorUserId().getValue(),
                entity.getTransactionType().name(),
                entity.getCategoryId().getValue(),
                entity.getCreatedAt().toString()
        );
    }
}

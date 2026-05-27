package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.commands.RegisterTransactionCommand;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.RegisterTransactionResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.*;

import java.math.BigDecimal;

/**
 * RegisterTransactionCommandFromResourceAssembler is a utility class that provides a method to convert a RegisterTransactionResource, which represents the data received from the REST API when registering a new transaction, into a RegisterTransactionCommand, which is used in the application layer to process the registration of the transaction. This assembler takes care of mapping the fields from the resource to the command, performing necessary transformations such as converting string representations of amounts to BigDecimal and mapping string representations of transaction types to their corresponding enum values.
 */
public class RegisterTransactionCommandFromResourceAssembler {

    /**
     * Converts a RegisterTransactionResource into a RegisterTransactionCommand. This method takes the data from the resource, performs necessary
     * transformations (such as converting the amount to a BigDecimal and mapping the transaction type to the corresponding enum), and constructs a new RegisterTransactionCommand that can be used in the application layer to register a new transaction.
     * @param resource The RegisterTransactionResource containing the data for the transaction to be registered. It includes fields such as amount, currency code, description, owner ID, financial account ID, actor user ID, transaction type, and category ID.
     * @return A RegisterTransactionCommand constructed from the provided resource, ready to be used in the application layer for processing the transaction registration. The command will contain all necessary information extracted and transformed from the resource to facilitate the registration of a new transaction in the system.
     */
    public static RegisterTransactionCommand toCommandFromResource(RegisterTransactionResource resource) {
        var amount = resource.amount();
        var currencyCode = CurrencyCodes.valueOf(resource.currencyCode());
        var money = new Money(amount, currencyCode);

        var financialAccountId = new FinancialAccountId(resource.financialAccountId());
        var actorUserId = new UserId(resource.actorUserId());
        var categoryId = new CategoryId(resource.categoryId());

        var transactionType = TransactionTypes.valueOf(resource.transactionType());

        return new RegisterTransactionCommand(
                money,
                resource.description(),
                resource.ownerId(),
                financialAccountId,
                actorUserId,
                transactionType,
                categoryId
        );
    }
}

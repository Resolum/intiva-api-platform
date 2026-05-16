package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.commands.UpdateTransactionAmountCommand;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.UpdateTransactionAmountResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionEntryId;

import java.math.BigDecimal;

/**
 * UpdateTransactionAmountCommandFromResourceAssembler is a utility class that provides a method to convert an UpdateTransactionAmountResource into an UpdateTransactionAmountCommand. This assembler is responsible for taking the data from the resource, which is typically received from a REST API request, and transforming it into a command object that can be used by the application to perform the necessary business logic for updating a transaction's amount.
 */
public class UpdateTransactionAmountCommandFromResourceAssembler {

    /**
     * Converts an UpdateTransactionAmountResource into an UpdateTransactionAmountCommand. This method takes the transaction ID and the resource containing the new amount and currency code, and constructs a command object that can be used to update the transaction amount in the system. It ensures that the amount is properly converted to a BigDecimal and that the currency code is validated against the defined CurrencyCodes enum.
     * @param id The ID of the transaction to be updated, represented as a Long. This ID is used to identify the specific transaction entry that needs to be updated with the new amount and currency code provided in the resource.
     * @param resource An instance of UpdateTransactionAmountResource that contains the new amount and currency code for the transaction. The resource is expected to have valid data, including a properly formatted amount and a valid currency code that matches one of the values defined in the CurrencyCodes enum.
     * @return An instance of UpdateTransactionAmountCommand that encapsulates the transaction ID, the new amount as a Money object, and the currency code. This command can then be used by the application to perform the necessary updates to the transaction in the system.
     */
    public static UpdateTransactionAmountCommand toCommandFromResource(Long id, UpdateTransactionAmountResource resource) {
        var transactionId = new TransactionEntryId(id);
        var amount = new BigDecimal(resource.amount());
        var currencyCode = CurrencyCodes.valueOf(resource.currencyCode());
        var money = new Money(amount, currencyCode);

        return new UpdateTransactionAmountCommand(
                transactionId,
                money
        );
    }
}

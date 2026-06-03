package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.commands.CreateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.RecurringFrequency;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.CreateRecurringTransactionResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.*;

/**
 * Converts recurring transaction REST requests into create commands.
 */
public class CreateRecurringTransactionCommandFromResourceAssembler {

    /**
     * Maps the resource fields into the create command used by the application layer.
     *
     * @param resource REST payload received from the client
     * @return create command built from the resource
     */
    public static CreateRecurringTransactionCommand toCommandFromResource(CreateRecurringTransactionResource resource) {
        var amount = new Money(resource.amount(), CurrencyCodes.fromString(resource.currencyCode()));
        var financialAccountId = new FinancialAccountId(resource.financialAccountId());
        var performedByUserId = new UserId(resource.performedByUserId());
        var categoryId = new CategoryId(resource.categoryId());
        var transactionType = TransactionTypes.valueOf(resource.transactionType().toUpperCase());
        var ownerType = OwnerTypes.valueOf(resource.ownerType().toUpperCase());
        var frequency = RecurringFrequency.valueOf(resource.frequency().toUpperCase());

        return new CreateRecurringTransactionCommand(
                amount,
                resource.description(),
                resource.ownerId(),
                financialAccountId,
                performedByUserId,
                transactionType,
                categoryId,
                ownerType,
                frequency,
                resource.startDate(),
                resource.endDate()
        );
    }
}

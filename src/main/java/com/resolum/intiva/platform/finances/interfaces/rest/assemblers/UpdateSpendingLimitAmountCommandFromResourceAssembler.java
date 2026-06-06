package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.commands.UpdateSpendingLimitAmountCommand;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.UpdateSpendingLimitAmountResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;

/**
 * Maps the update amount REST payload to the corresponding domain command.
 */
public class UpdateSpendingLimitAmountCommandFromResourceAssembler {

    public static UpdateSpendingLimitAmountCommand toCommandFromResource(Long spendingLimitId, UpdateSpendingLimitAmountResource resource) {
        var currencyCode = CurrencyCodes.fromString(resource.currencyCode());
        var money = new Money(resource.limitAmount(), currencyCode);

        return new UpdateSpendingLimitAmountCommand(spendingLimitId, money);
    }
}

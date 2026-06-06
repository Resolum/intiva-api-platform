package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.commands.CreateSpendingLimitCommand;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.CreateSpendingLimitResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

/**
 * Maps the create spending limit REST payload to the corresponding domain command.
 */
public class CreateSpendingLimitCommandFromResourceAssembler {

    public static CreateSpendingLimitCommand toCommandFromResource(CreateSpendingLimitResource resource) {
        var currencyCode = CurrencyCodes.fromString(resource.currencyCode());
        var money = new Money(resource.limitAmount(), currencyCode);

        return new CreateSpendingLimitCommand(
                resource.ownerId(),
                OwnerTypes.valueOf(resource.ownerType().toUpperCase()),
                SpendingLimitTargetType.valueOf(resource.targetType().toUpperCase()),
                resource.targetId(),
                money,
                resource.startDate(),
                resource.endDate()
        );
    }
}

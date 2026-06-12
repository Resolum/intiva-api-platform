package com.resolum.intiva.platform.finances.interfaces.rest.assemblers;

import com.resolum.intiva.platform.finances.domain.model.commands.UpdateSpendingLimitPeriodCommand;
import com.resolum.intiva.platform.finances.interfaces.rest.resources.requests.UpdateSpendingLimitPeriodResource;

/**
 * Maps the update period REST payload to the corresponding domain command.
 */
public class UpdateSpendingLimitPeriodCommandFromResourceAssembler {

    public static UpdateSpendingLimitPeriodCommand toCommandFromResource(Long spendingLimitId, UpdateSpendingLimitPeriodResource resource) {
        return new UpdateSpendingLimitPeriodCommand(spendingLimitId, resource.startDate(), resource.endDate());
    }
}

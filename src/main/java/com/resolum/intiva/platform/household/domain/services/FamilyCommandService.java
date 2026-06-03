package com.resolum.intiva.platform.household.domain.services;

import com.resolum.intiva.platform.household.domain.model.aggregates.Family;
import com.resolum.intiva.platform.household.domain.model.commands.CreateFamilyCommand;

public interface FamilyCommandService {
    Family handle(CreateFamilyCommand command);
}

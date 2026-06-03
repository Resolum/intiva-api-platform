package com.resolum.intiva.platform.household.domain.services;

import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.domain.model.commands.AssignRoleCommand;

public interface FamilyMemberCommandService {
    FamilyMember handle(AssignRoleCommand command);
}

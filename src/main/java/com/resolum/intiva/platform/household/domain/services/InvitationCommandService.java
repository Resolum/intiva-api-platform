package com.resolum.intiva.platform.household.domain.services;

import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.domain.model.commands.AcceptInvitationCommand;
import com.resolum.intiva.platform.household.domain.model.commands.RejectInvitationCommand;

public interface InvitationCommandService {
    Invitation handle(AcceptInvitationCommand command);
    Invitation handle(RejectInvitationCommand command);
}

package com.resolum.intiva.platform.household.domain.services;

import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.domain.model.commands.AcceptInvitationCommand;
import com.resolum.intiva.platform.household.domain.model.commands.RejectInvitationCommand;
import com.resolum.intiva.platform.household.domain.model.commands.SendInvitationCommand;

public interface InvitationCommandService {
    Invitation handle(AcceptInvitationCommand command);
    Invitation handle(RejectInvitationCommand command);
    Invitation handle(SendInvitationCommand command);
}

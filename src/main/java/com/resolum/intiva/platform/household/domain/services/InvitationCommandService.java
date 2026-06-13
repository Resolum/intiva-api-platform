package com.resolum.intiva.platform.household.domain.services;

import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.domain.model.commands.AcceptInvitationCommand;
import com.resolum.intiva.platform.household.domain.model.commands.ClaimDeferredInviteCommand;
import com.resolum.intiva.platform.household.domain.model.commands.RejectInvitationCommand;
import com.resolum.intiva.platform.household.domain.model.commands.SendInvitationCommand;
import com.resolum.intiva.platform.household.domain.model.commands.SendInvitationLinkCommand;
import com.resolum.intiva.platform.household.application.internal.InvitationLinkResult;

public interface InvitationCommandService {
    Invitation handle(AcceptInvitationCommand command);
    void handle(RejectInvitationCommand command);
    Invitation handle(SendInvitationCommand command);
    InvitationLinkResult sendInvitationLink(SendInvitationLinkCommand command);
    void claimDeferredInvite(ClaimDeferredInviteCommand command);
}

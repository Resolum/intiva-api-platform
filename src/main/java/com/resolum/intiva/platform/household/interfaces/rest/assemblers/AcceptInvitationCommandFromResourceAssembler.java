package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.commands.AcceptInvitationCommand;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Assembler that creates an AcceptInvitationCommand from path and principal data.
 */
public class AcceptInvitationCommandFromResourceAssembler {

    /**
     * Converts path and principal data into an AcceptInvitationCommand.
     *
     * @param invitationId the ID of the invitation to accept, taken from the path variable
     * @param userId       the numeric user ID of the person accepting, taken from the principal
     * @return the corresponding AcceptInvitationCommand
     */
    public static AcceptInvitationCommand toCommandFromResource(Long invitationId, Long userId) {
        return new AcceptInvitationCommand(invitationId, new UserId(userId));
    }
}

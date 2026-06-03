package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.commands.RejectInvitationCommand;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Assembler that creates a RejectInvitationCommand from path and principal data.
 */
public class RejectInvitationCommandFromResourceAssembler {

    /**
     * Converts path and principal data into a RejectInvitationCommand.
     *
     * @param invitationId the ID of the invitation to reject, taken from the path variable
     * @param userId       the numeric user ID of the person rejecting, taken from the principal
     * @return the corresponding RejectInvitationCommand
     */
    public static RejectInvitationCommand toCommandFromResource(Long invitationId, Long userId) {
        return new RejectInvitationCommand(invitationId, new UserId(userId));
    }
}

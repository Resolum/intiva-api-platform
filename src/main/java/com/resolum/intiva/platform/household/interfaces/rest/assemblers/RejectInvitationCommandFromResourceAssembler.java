package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.commands.RejectInvitationCommand;

/**
 * Assembler that creates a RejectInvitationCommand from path and principal data.
 */
public class RejectInvitationCommandFromResourceAssembler {

    /**
     * Converts path and optional principal data into a RejectInvitationCommand.
     *
     * @param token        the invitation token, taken from the path variable
     * @param rejectorId   the numeric user ID of the person rejecting, or null if unauthenticated
     * @param rejectorName the display name of the person rejecting, or null
     * @return the corresponding RejectInvitationCommand
     */
    public static RejectInvitationCommand toCommandFromResource(String token, Long rejectorId, String rejectorName) {
        return new RejectInvitationCommand(token, rejectorId, rejectorName);
    }
}

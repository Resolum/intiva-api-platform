package com.resolum.intiva.platform.iam.interfaces.rest.assemblers;

import com.resolum.intiva.platform.iam.domain.model.commands.SignUpCommand;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.requests.SignUpResource;

/**
 * Assembler class to convert SignUpResource to SignUpCommand.
 */
public class SignUpCommandFromResourceAssembler {

    /**
     * Static method to convert SignUpResource to SignUpCommand
     * @param resource the SignUpResource to convert
     * @return the converted SignUpCommand
     */
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        var username = new Email(resource.email());

        return new SignUpCommand(
                username,
                resource.password()
        );
    }
}

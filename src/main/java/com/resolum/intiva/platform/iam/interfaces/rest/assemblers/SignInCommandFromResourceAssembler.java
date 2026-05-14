package com.resolum.intiva.platform.iam.interfaces.rest.assemblers;

import com.resolum.intiva.platform.iam.domain.model.commands.SignInCommand;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.PasswordHash;
import com.resolum.intiva.platform.iam.interfaces.rest.resources.requests.SignInResource;

/**
 * Assembler class to convert SignInResource to SignInCommand.
 */
public class SignInCommandFromResourceAssembler {

    /**
     * Private constructor to prevent instantiation of the assembler class.
     */
    private SignInCommandFromResourceAssembler() {
    }

    /**
     * Static method to convert SignInResource to SignInCommand
     * @param resource the SignInResource to convert
     * @return the converted SignInCommand
     */
    public static SignInCommand toCommandFromResource(SignInResource resource) {

        return new SignInCommand(
                new Email(resource.email()),
                new PasswordHash(resource.password())
        );
    }
}

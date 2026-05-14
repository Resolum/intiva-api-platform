package com.resolum.intiva.platform.iam.domain.services;

import com.resolum.intiva.platform.iam.domain.model.commands.SeedRolesCommand;

/**
 * Service interface for handling role-related commands in the IAM domain.
 */
public interface RoleCommandService {

    /**
     * Seeds the roles in the system.
     * @param command the command containing the details for seeding roles
     */
    void handle(SeedRolesCommand command);
}

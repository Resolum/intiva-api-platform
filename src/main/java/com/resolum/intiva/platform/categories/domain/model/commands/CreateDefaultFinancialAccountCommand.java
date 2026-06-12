package com.resolum.intiva.platform.categories.domain.model.commands;

/**
 * Command object for creating a default financial account.
 * @param ownerId the ID of the owner of the default financial account
 */
public record CreateDefaultFinancialAccountCommand(Long ownerId) {
    public CreateDefaultFinancialAccountCommand {
        if (ownerId == null) {
            throw new IllegalArgumentException("The owner ID cannot be null.");
        }
    }
}

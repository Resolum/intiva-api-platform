package com.resolum.intiva.platform.categories.domain.model.commands;

/**
 * Command to create default categories for a user. This command is used when a new user is registered to automatically set up default categories for them.
 */
public record CreateDefaultCategoryCommand(Long userId) {
    public CreateDefaultCategoryCommand {
        if (userId == null) {
            throw new IllegalArgumentException("The user ID cannot be null.");
        }
    }
}

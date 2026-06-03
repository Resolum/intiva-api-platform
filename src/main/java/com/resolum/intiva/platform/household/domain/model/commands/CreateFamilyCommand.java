package com.resolum.intiva.platform.household.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;

/**
 * Command to create a new family group.
 *
 * @param name        the name of the family group
 * @param description an optional description of the family group
 * @param ownerId     the UserId of the user creating and owning the group
 */
public record CreateFamilyCommand(
        String name,
        String description,
        UserId ownerId
) {
}

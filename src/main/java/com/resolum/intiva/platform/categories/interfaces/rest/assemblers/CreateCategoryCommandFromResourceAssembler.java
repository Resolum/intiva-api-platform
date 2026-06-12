package com.resolum.intiva.platform.categories.interfaces.rest.assemblers;

import com.resolum.intiva.platform.categories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryType;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.requests.CreateCategoryResource;

/**
 * Assembler class to convert CreateCategoryResource to CreateCategoryCommand.
 */
public class CreateCategoryCommandFromResourceAssembler {

    /**
     * Converts a CreateCategoryResource to CreateCategoryCommand.
     *
     * @param resource the CreateCategoryResource to convert.
     * @return a CreateCategoryCommand containing the data from the resource.
     */
    public static CreateCategoryCommand toCommandFromResource(CreateCategoryResource resource) {
        CategoryType type;

        try {
            type = CategoryType.valueOf(resource.type().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new IllegalArgumentException("Invalid category type: " + resource.type());
        }

        return new CreateCategoryCommand(
                resource.name(),
                resource.ownerType(),
                resource.ownerId(),
                resource.description(),
                resource.color(),
                resource.icon(),
                type
        );
    }
}
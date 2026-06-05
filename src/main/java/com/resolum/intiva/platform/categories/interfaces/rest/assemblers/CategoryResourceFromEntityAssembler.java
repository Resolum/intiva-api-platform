package com.resolum.intiva.platform.categories.interfaces.rest.assemblers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.responses.CategoryResource;

/**
 * Assembler class responsible for converting Category entities to CategoryResource objects for API responses.
 */
public class CategoryResourceFromEntityAssembler {

    /**
     * Converts a Category entity to a CategoryResource for API responses.
     *
     * @param entity The Category entity to be converted.
     * @return A CategoryResource containing the relevant data from the Category entity.
     */
    public static CategoryResource toResourceFromEntity(Category entity) {
        return new CategoryResource(
                entity.getId(),
                entity.getName(),
                entity.getOwnerType(),
                entity.getOwnerId(),
                entity.getIsActive(),
                entity.getDescription().getDescription(),
                entity.getColor().getColor(),
                entity.getIcon().getIcon()
        );
    }
}

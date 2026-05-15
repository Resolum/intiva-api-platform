package com.resolum.intiva.platform.categories.interfaces.rest.assemblers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.categories.interfaces.rest.resources.responses.CategoryResource;

public class CategoryResourceFromEntityAssembler {
    public static CategoryResource toResourceFromEntity(Category entity) {
        return new CategoryResource(
                entity.getId(),
                entity.getName(),
                entity.getColor(),
                entity.getOwnerType(),
                entity.getUserId(),
                entity.getGroupId(),
                entity.getIsActive()
        );
    }
}
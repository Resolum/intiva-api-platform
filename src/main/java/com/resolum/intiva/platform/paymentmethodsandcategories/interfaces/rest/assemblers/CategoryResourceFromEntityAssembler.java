package com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.assemblers;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.aggregates.Category;
import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.resources.responses.CategoryResource;

public class CategoryResourceFromEntityAssembler {
    public static CategoryResource toResourceFromEntity(Category entity) {
        return new CategoryResource(
                entity.getId(),
                entity.getName(),
                entity.getOwnerType(),
                entity.getUserId(),
                entity.getGroupId(),
                entity.getIsActive(),
                entity.getDescription().getDescription(),
                entity.getColor().getColor(),
                entity.getIcon().getIcon()
        );
    }
}
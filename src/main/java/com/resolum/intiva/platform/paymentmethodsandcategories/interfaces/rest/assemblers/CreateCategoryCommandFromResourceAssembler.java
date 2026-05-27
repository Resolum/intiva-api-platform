package com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.assemblers;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.resources.requests.CreateCategoryResource;

public class CreateCategoryCommandFromResourceAssembler {
    public static CreateCategoryCommand toCommandFromResource(CreateCategoryResource resource) {
        return new CreateCategoryCommand(
                resource.name(),
                resource.ownerType(),
                resource.userId(),
                resource.groupId(),
                resource.description(),
                resource.color(),
                resource.icon()
        );
    }
}
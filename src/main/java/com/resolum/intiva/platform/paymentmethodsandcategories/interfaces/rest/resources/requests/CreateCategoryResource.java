package com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.rest.resources.requests;

public record CreateCategoryResource(
        String name,
        String ownerType,
        Long userId,
        Long groupId,
        String description,
        String color,
        String icon
) {}
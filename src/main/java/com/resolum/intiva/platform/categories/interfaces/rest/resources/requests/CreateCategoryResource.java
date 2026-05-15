package com.resolum.intiva.platform.categories.interfaces.rest.resources.requests;

public record CreateCategoryResource(
        String name,
        String color,
        String ownerType,
        Long userId, //
        Long groupId //
) {}
package com.resolum.intiva.platform.categories.interfaces.rest.resources.responses;

public record CategoryResource(
        Long id,
        String name,
        String color,
        String ownerType,
        Long userId, //
        Long groupId, //
        Boolean isActive
) {}
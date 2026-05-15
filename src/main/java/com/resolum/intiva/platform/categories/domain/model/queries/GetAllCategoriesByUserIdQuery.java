package com.resolum.intiva.platform.categories.domain.model.queries;

public record GetAllCategoriesByUserIdQuery(Long userId) {
    public GetAllCategoriesByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
    }
}
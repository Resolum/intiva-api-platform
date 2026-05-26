package com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.queries;

public record GetCategoryByIdQuery(Long id) { // <--- Cambiar a Long
    public GetCategoryByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la categoría no puede ser nulo");
        }
    }
}
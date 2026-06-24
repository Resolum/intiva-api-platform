package com.resolum.intiva.platform.profiles.domain.model.queries;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetProfileByUserIdQueryTests {

    // Este test verifica que el record GetProfileByUserIdQuery almacena el userId correctamente.
    // El query es un carrier simple y debe preservar el valor recibido sin modificaciones.
    @Test
    void shouldStoreUserIdCorrectly() {
        var query = new GetProfileByUserIdQuery(7L);

        assertEquals(7L, query.userId());
    }

    // Este test verifica que dos instancias de GetProfileByUserIdQuery con el mismo
    // userId son iguales en términos de valor. Es necesario para comparaciones en los servicios.
    @Test
    void shouldBeEqualWhenSameUserId() {
        var q1 = new GetProfileByUserIdQuery(5L);
        var q2 = new GetProfileByUserIdQuery(5L);

        assertEquals(q1, q2);
    }

    // Este test verifica que dos instancias de GetProfileByUserIdQuery con diferente userId
    // no son iguales. Cada query identifica a un usuario diferente y no deben confundirse.
    @Test
    void shouldNotBeEqualWhenDifferentUserId() {
        var q1 = new GetProfileByUserIdQuery(1L);
        var q2 = new GetProfileByUserIdQuery(2L);

        assertNotEquals(q1, q2);
    }

    // Este test verifica que el query acepta userId con valores grandes (Long).
    // La base de datos puede generar IDs altos y el query debe transportarlos sin pérdida.
    @Test
    void shouldAcceptLargeUserIdValues() {
        var query = new GetProfileByUserIdQuery(Long.MAX_VALUE);

        assertEquals(Long.MAX_VALUE, query.userId());
    }
}
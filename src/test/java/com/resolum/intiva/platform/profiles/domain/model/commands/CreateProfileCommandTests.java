package com.resolum.intiva.platform.profiles.domain.model.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateProfileCommandTests {

    // Este test verifica que el record CreateProfileCommand almacena correctamente
    // el userId y el name que se pasan al momento de la construcción.
    // Es la validación básica de que el command carrier no pierde datos.
    @Test
    void shouldStoreUserIdAndName() {
        var command = new CreateProfileCommand(5L, "Juan Pérez");

        assertEquals(5L, command.userId());
        assertEquals("Juan Pérez", command.name());
    }

    // Este test verifica que dos instancias de CreateProfileCommand con los mismos
    // valores son iguales. Los records en Java deben implementar equals por valor,
    // lo que es necesario para comparaciones en tests de integración y asserts.
    @Test
    void shouldBeEqualWhenSameValues() {
        var cmd1 = new CreateProfileCommand(1L, "Ana Torres");
        var cmd2 = new CreateProfileCommand(1L, "Ana Torres");

        assertEquals(cmd1, cmd2);
    }

    // Este test verifica que dos instancias de CreateProfileCommand con userId diferente
    // no son iguales. Es importante confirmar que el equals distingue por contenido.
    @Test
    void shouldNotBeEqualWhenDifferentUserId() {
        var cmd1 = new CreateProfileCommand(1L, "Carlos");
        var cmd2 = new CreateProfileCommand(2L, "Carlos");

        assertNotEquals(cmd1, cmd2);
    }

    // Este test verifica que CreateProfileCommand acepta un nombre con espacios y
    // caracteres especiales. Los nombres de usuario pueden tener acentos u otros
    // caracteres propios del español que deben preservarse.
    @Test
    void shouldAcceptNameWithAccentsAndSpaces() {
        var command = new CreateProfileCommand(1L, "María José Ávila");

        assertEquals("María José Ávila", command.name());
    }
}
package com.resolum.intiva.platform.profiles.domain.model.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateProfileCommandTests {

    // Este test verifica que el record UpdateProfileCommand almacena correctamente
    // todos los campos: userId, name, bio, phoneNumber y age.
    // Validar el command completo garantiza que no se pierden datos antes de llegar al servicio.
    @Test
    void shouldStoreAllFieldsCorrectly() {
        var command = new UpdateProfileCommand(10L, "Pedro Salas", "Dev en Lima", "+51987000000", 28);

        assertEquals(10L, command.userId());
        assertEquals("Pedro Salas", command.name());
        assertEquals("Dev en Lima", command.bio());
        assertEquals("+51987000000", command.phoneNumber());
        assertEquals(28, command.age());
    }

    // Este test verifica que UpdateProfileCommand acepta null en los campos opcionales
    // como bio, phoneNumber y age. El servicio trata el null como "no actualizar ese campo",
    // así que el command debe poder transportarlo sin errores.
    @Test
    void shouldAcceptNullOptionalFields() {
        var command = new UpdateProfileCommand(1L, "Solo Nombre", null, null, null);

        assertEquals("Solo Nombre", command.name());
        assertNull(command.bio());
        assertNull(command.phoneNumber());
        assertNull(command.age());
    }

    // Este test verifica que dos UpdateProfileCommand con los mismos valores son iguales.
    // La igualdad por valor en records es esencial para verificaciones en capas superiores.
    @Test
    void shouldBeEqualWhenSameValues() {
        var cmd1 = new UpdateProfileCommand(3L, "Rosa", "Bio", "+511234567", 30);
        var cmd2 = new UpdateProfileCommand(3L, "Rosa", "Bio", "+511234567", 30);

        assertEquals(cmd1, cmd2);
    }

    // Este test verifica que UpdateProfileCommand con userId diferente no son iguales.
    // El userId identifica al propietario del perfil y debe diferenciar los commands.
    @Test
    void shouldNotBeEqualWhenUserIdDiffers() {
        var cmd1 = new UpdateProfileCommand(1L, "Rosa", "Bio", "+511234567", 30);
        var cmd2 = new UpdateProfileCommand(2L, "Rosa", "Bio", "+511234567", 30);

        assertNotEquals(cmd1, cmd2);
    }

    // Este test verifica que UpdateProfileCommand con un age de cero funciona correctamente.
    // Los usuarios recién creados pueden tener age = 0 como valor por defecto inicial,
    // por lo que no debe ser un valor prohibido en el command.
    @Test
    void shouldAcceptZeroAsAge() {
        var command = new UpdateProfileCommand(1L, "Nuevo Usuario", "", "", 0);

        assertEquals(0, command.age());
    }
}
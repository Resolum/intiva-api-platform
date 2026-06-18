package com.resolum.intiva.platform.profiles.domain.model.aggregates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTests {

    // Este test verifica que un perfil se crea correctamente con datos válidos.
    // Es el caso básico que confirma que el aggregate Profile puede instanciarse
    // con userId, nombre, edad y fecha de nacimiento sin lanzar excepciones.
    @Test
    void shouldCreateProfileWithValidData() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Juan Pérez")
                .age(25)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        assertNotNull(profile);
        assertEquals("Juan Pérez", profile.getName());
        assertEquals(25, profile.getAge());
        assertEquals(1L, profile.getUserId().userId());
    }

    // Este test verifica que el perfil asigna el avatar por defecto cuando
    // no se proporciona ninguna URL de imagen al momento de la creación.
    // Esto es importante porque todos los perfiles nuevos deben tener una imagen visible.
    @Test
    void shouldApplyDefaultAvatarWhenNoUrlProvided() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Ana García")
                .age(22)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        assertNotNull(profile.getAvatarUrl());
        assertTrue(profile.hasDefaultAvatar());
    }

    // Este test verifica que el perfil usa el avatar por defecto también
    // cuando se pasa una cadena en blanco como URL. El aggregate debe manejar
    // este caso igual que cuando la URL es null.
    @Test
    void shouldApplyDefaultAvatarWhenBlankUrlProvided() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Carlos López")
                .age(30)
                .birthDate(null)
                .avatarUrl("   ")
                .publicId(null)
                .build();

        assertTrue(profile.hasDefaultAvatar());
    }

    // Este test verifica que el perfil almacena un avatar personalizado
    // cuando se proporciona una URL válida. El método hasDefaultAvatar debe
    // retornar false porque el publicId no coincide con el de la imagen por defecto.
    @Test
    void shouldStoreCustomAvatarWhenUrlIsProvided() {
        var profile = Profile.builder()
                .userId(1L)
                .name("María Torres")
                .age(28)
                .birthDate(null)
                .avatarUrl("https://res.cloudinary.com/test/image/upload/custom.png")
                .publicId("custom_avatar_xyz")
                .build();

        assertFalse(profile.hasDefaultAvatar());
        assertEquals("https://res.cloudinary.com/test/image/upload/custom.png",
                profile.getAvatarUrl().getUrl());
    }

    // Este test verifica que la creación de un perfil lanza IllegalArgumentException
    // cuando el userId es null. El aggregate debe proteger el invariante de que
    // todo perfil debe estar vinculado a un usuario existente.
    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                Profile.builder()
                        .userId(null)
                        .name("Test User")
                        .age(20)
                        .birthDate(null)
                        .avatarUrl(null)
                        .publicId(null)
                        .build()
        );
    }

    // Este test verifica que la creación de un perfil lanza IllegalArgumentException
    // cuando el nombre es null. El nombre es un campo obligatorio y el aggregate
    // debe rechazar perfiles sin identidad visible.
    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                Profile.builder()
                        .userId(1L)
                        .name(null)
                        .age(20)
                        .birthDate(null)
                        .avatarUrl(null)
                        .publicId(null)
                        .build()
        );
    }

    // Este test verifica que la creación de un perfil lanza IllegalArgumentException
    // cuando el nombre está en blanco. Un nombre vacío no identifica a ningún usuario
    // y no debe permitirse en el dominio.
    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                Profile.builder()
                        .userId(1L)
                        .name("   ")
                        .age(20)
                        .birthDate(null)
                        .avatarUrl(null)
                        .publicId(null)
                        .build()
        );
    }

    // Este test verifica que el perfil se inicializa con phoneNumber y bio vacíos.
    // Al crear el perfil por primera vez, esos campos no son obligatorios y deben
    // comenzar como cadenas vacías para que la API no retorne null en esos campos.
    @Test
    void shouldInitializePhoneNumberAndBioAsEmpty() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Luis Mendoza")
                .age(24)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        assertEquals("", profile.getPhoneNumber());
        assertEquals("", profile.getBio());
    }

    // Este test verifica que updatePersonalInfo actualiza el nombre del perfil correctamente.
    // El nombre se puede cambiar en cualquier momento y el aggregate debe reflejar el nuevo valor.
    @Test
    void shouldUpdateProfileNameWhenNotBlank() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Juan Original")
                .age(25)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        profile.updatePersonalInfo("Juan Actualizado", null, null, null);

        assertEquals("Juan Actualizado", profile.getName());
    }

    // Este test verifica que updatePersonalInfo no modifica el nombre cuando se pasa null.
    // Si el cliente no envía un nuevo nombre, el perfil debe mantener el valor actual.
    @Test
    void shouldKeepExistingNameWhenUpdateNameIsNull() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Nombre Actual")
                .age(25)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        profile.updatePersonalInfo(null, "nueva bio", "+51999999999", 26);

        assertEquals("Nombre Actual", profile.getName());
    }

    // Este test verifica que updatePersonalInfo actualiza correctamente la bio y phoneNumber.
    // Estos campos son opcionales pero editables, y deben reflejar los valores enviados por el usuario.
    @Test
    void shouldUpdateBioAndPhoneNumber() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Pedro Ruiz")
                .age(30)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        profile.updatePersonalInfo(null, "Desarrollador backend", "+51987654321", null);

        assertEquals("Desarrollador backend", profile.getBio());
        assertEquals("+51987654321", profile.getPhoneNumber());
    }

    // Este test verifica que updatePersonalInfo actualiza la edad cuando se proporciona un valor.
    // La edad puede cambiar y el perfil debe almacenar el valor más reciente.
    @Test
    void shouldUpdateAgeWhenNotNull() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Sofía Vargas")
                .age(20)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        profile.updatePersonalInfo(null, null, null, 21);

        assertEquals(21, profile.getAge());
    }

    // Este test verifica que updateAvatar reemplaza la imagen del perfil con una nueva URL y publicId.
    // Cuando el usuario sube una nueva foto, el aggregate debe actualizar ambos campos del ImageURL.
    @Test
    void shouldUpdateAvatarWithNewUrlAndPublicId() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Elena Castillo")
                .age(27)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        profile.updateAvatar("https://res.cloudinary.com/test/new_avatar.png", "new_avatar_abc");

        assertFalse(profile.hasDefaultAvatar());
        assertEquals("https://res.cloudinary.com/test/new_avatar.png",
                profile.getAvatarUrl().getUrl());
    }

    // Este test verifica que hasDefaultAvatar retorna true cuando el perfil aún
    // tiene el avatar por defecto asignado al momento de la creación.
    @Test
    void shouldReturnTrueForHasDefaultAvatarOnNewProfile() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Roberto Díaz")
                .age(33)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        assertTrue(profile.hasDefaultAvatar());
    }

    // Este test verifica que hasDefaultAvatar retorna false después de actualizar
    // el avatar con una imagen personalizada. Esto es importante para que el servicio
    // sepa si debe borrar la imagen anterior en Cloudinary antes de subir la nueva.
    @Test
    void shouldReturnFalseForHasDefaultAvatarAfterCustomUpload() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Camila Herrera")
                .age(29)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        profile.updateAvatar("https://res.cloudinary.com/custom.jpg", "custom_abc123");

        assertFalse(profile.hasDefaultAvatar());
    }

    // Este test verifica que updatePersonalInfo retorna la misma instancia del perfil (this).
    // El método usa el patrón fluent para encadenar llamadas, por lo que debe retornar self.
    @Test
    void shouldReturnSameInstanceAfterUpdatePersonalInfo() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Diego Paredes")
                .age(26)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        var result = profile.updatePersonalInfo("Diego P.", "Dev", "+51000000001", 27);

        assertSame(profile, result);
    }

    // Este test verifica que updateAvatar retorna la misma instancia del perfil.
    // El método también usa el patrón fluent y debe retornar self para encadenamiento.
    @Test
    void shouldReturnSameInstanceAfterUpdateAvatar() {
        var profile = Profile.builder()
                .userId(1L)
                .name("Valeria Ríos")
                .age(23)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();

        var result = profile.updateAvatar("https://url.com/img.jpg", "pub123");

        assertSame(profile, result);
    }
}
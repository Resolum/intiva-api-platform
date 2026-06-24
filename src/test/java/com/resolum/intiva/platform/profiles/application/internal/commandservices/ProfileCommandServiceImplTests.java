package com.resolum.intiva.platform.profiles.application.internal.commandservices;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.mock.web.MockMultipartFile;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Profile;
import com.resolum.intiva.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.resolum.intiva.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.resolum.intiva.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import com.resolum.intiva.platform.shared.application.internal.outboundservices.filestorage.ImageService;

/**
 * Unit tests para {@link ProfileCommandServiceImpl}.
 */
class ProfileCommandServiceImplTests {

    // ─── helpers ──────────────────────────────────────────────────────────────

    private Profile buildProfile(Long userId, String name) {
        return Profile.builder()
                .userId(userId)
                .name(name)
                .age(0)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();
    }

    // ─── handle(CreateProfileCommand) ─────────────────────────────────────────

    // Este test verifica que handle(CreateProfileCommand) crea y persiste un nuevo perfil.
    // El servicio debe construir el aggregate con los datos del command y guardarlo en el repositorio.
    // Es el flujo principal que se ejecuta automáticamente al registrar un nuevo usuario.
    @Test
    void handleCreate_shouldPersistAndReturnNewProfile() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var imageService = mock(ImageService.class);
        var service = new ProfileCommandServiceImpl(repository, imageService);
        var command = new CreateProfileCommand(1L, "Ana García");

        when(repository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = service.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals("Ana García", result.getName());
        assertEquals(1L, result.getUserId().userId());
        verify(repository).save(any(Profile.class));
    }

    // Este test verifica que el perfil creado por handle(CreateProfileCommand) tiene
    // el avatar por defecto asignado, ya que al crearse automáticamente no se provee imagen.
    @Test
    void handleCreate_shouldAssignDefaultAvatar() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var imageService = mock(ImageService.class);
        var service = new ProfileCommandServiceImpl(repository, imageService);
        var command = new CreateProfileCommand(2L, "Carlos Ruiz");

        when(repository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = service.handle(command);

        // Assert
        assertTrue(result.hasDefaultAvatar());
    }

    // Este test verifica que handle(CreateProfileCommand) llama a repository.save exactamente
    // una vez. El servicio no debe duplicar inserciones ni llamar al repositorio de más.
    @Test
    void handleCreate_shouldCallSaveExactlyOnce() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var imageService = mock(ImageService.class);
        var service = new ProfileCommandServiceImpl(repository, imageService);
        var command = new CreateProfileCommand(3L, "María López");

        when(repository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.handle(command);

        // Assert
        verify(repository, times(1)).save(any(Profile.class));
    }

    // ─── handle(UpdateProfileCommand) ─────────────────────────────────────────

    // Este test verifica que handle(UpdateProfileCommand) actualiza y persiste el perfil
    // cuando el usuario existe. El servicio debe encontrar el perfil, aplicar los cambios
    // y guardar el estado actualizado en el repositorio.
    @Test
    void handleUpdate_shouldUpdateAndReturnProfile_whenUserExists() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var imageService = mock(ImageService.class);
        var service = new ProfileCommandServiceImpl(repository, imageService);
        var existing = buildProfile(1L, "Nombre Viejo");
        var command = new UpdateProfileCommand(1L, "Nombre Nuevo", "Bio actualizada", "+51900000001", 26);

        when(repository.findByUserId_UserId(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        // Act
        var result = service.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Nombre Nuevo", result.get().getName());
        assertEquals("Bio actualizada", result.get().getBio());
        assertEquals("+51900000001", result.get().getPhoneNumber());
        assertEquals(26, result.get().getAge());
        verify(repository).save(existing);
    }

    // Este test verifica que handle(UpdateProfileCommand) retorna Optional.empty()
    // cuando no existe un perfil para el userId proporcionado.
    // El controlador usa este resultado para retornar HTTP 404.
    @Test
    void handleUpdate_shouldReturnEmpty_whenProfileDoesNotExist() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var imageService = mock(ImageService.class);
        var service = new ProfileCommandServiceImpl(repository, imageService);
        var command = new UpdateProfileCommand(99L, "Alguien", null, null, null);

        when(repository.findByUserId_UserId(99L)).thenReturn(Optional.empty());

        // Act
        var result = service.handle(command);

        // Assert
        assertTrue(result.isEmpty());
        verify(repository, never()).save(any());
    }

    // Este test verifica que handle(UpdateProfileCommand) no modifica el nombre
    // cuando el comando trae name = null. Solo deben actualizarse los campos no nulos.
    @Test
    void handleUpdate_shouldNotChangeName_whenCommandNameIsNull() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var imageService = mock(ImageService.class);
        var service = new ProfileCommandServiceImpl(repository, imageService);
        var existing = buildProfile(1L, "Nombre Original");
        var command = new UpdateProfileCommand(1L, null, "nueva bio", null, null);

        when(repository.findByUserId_UserId(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        // Act
        var result = service.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Nombre Original", result.get().getName());
    }

    // ─── handleAvatarUpdate ────────────────────────────────────────────────────

    // Este test verifica que handleAvatarUpdate sube la nueva imagen y actualiza el perfil
    // cuando el usuario existe y tiene el avatar por defecto.
    // El servicio no debe intentar borrar el avatar por defecto de Cloudinary antes de subir.
    @Test
    void handleAvatarUpdate_shouldUploadImageAndUpdateProfile_whenDefaultAvatar() throws Exception {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var imageService = mock(ImageService.class);
        var service = new ProfileCommandServiceImpl(repository, imageService);
        var existing = buildProfile(1L, "Lucas Vera");
        var file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "fake-bytes".getBytes());

        when(repository.findByUserId_UserId(1L)).thenReturn(Optional.of(existing));
        when(imageService.upload(any(byte[].class), anyString()))
                .thenReturn(Map.of("url", "https://cloud.com/new.jpg", "publicId", "new_abc"));
        when(repository.save(existing)).thenReturn(existing);

        // Act
        var result = service.handleAvatarUpdate(1L, file);

        // Assert
        assertTrue(result.isPresent());
        assertFalse(result.get().hasDefaultAvatar());
        verify(imageService, never()).delete(anyString());
        verify(imageService).upload(any(byte[].class), anyString());
        verify(repository).save(existing);
    }

    // Este test verifica que handleAvatarUpdate borra el avatar anterior en Cloudinary
    // antes de subir uno nuevo cuando el perfil ya tiene un avatar personalizado.
    // Esto evita imágenes huérfanas acumuladas en el almacenamiento.
    @Test
    void handleAvatarUpdate_shouldDeleteOldAvatarBeforeUpload_whenCustomAvatarExists() throws Exception {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var imageService = mock(ImageService.class);
        var service = new ProfileCommandServiceImpl(repository, imageService);
        var existing = buildProfile(1L, "Sofía Quispe");
        existing.updateAvatar("https://cloud.com/old.jpg", "old_pub_id_xyz");
        var file = new MockMultipartFile("file", "new.jpg", "image/jpeg", "bytes".getBytes());

        when(repository.findByUserId_UserId(1L)).thenReturn(Optional.of(existing));
        when(imageService.upload(any(byte[].class), anyString()))
                .thenReturn(Map.of("url", "https://cloud.com/new.jpg", "publicId", "new_pub_id_abc"));
        when(repository.save(existing)).thenReturn(existing);

        // Act
        var result = service.handleAvatarUpdate(1L, file);

        // Assert
        assertTrue(result.isPresent());
        verify(imageService).delete("old_pub_id_xyz");
        verify(imageService).upload(any(byte[].class), anyString());
    }

    // Este test verifica que handleAvatarUpdate retorna Optional.empty() cuando
    // no se encuentra un perfil para el userId. El controlador lo convierte en HTTP 404.
    @Test
    void handleAvatarUpdate_shouldReturnEmpty_whenProfileNotFound() throws Exception {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var imageService = mock(ImageService.class);
        var service = new ProfileCommandServiceImpl(repository, imageService);
        var file = new MockMultipartFile("file", "img.jpg", "image/jpeg", "data".getBytes());

        when(repository.findByUserId_UserId(99L)).thenReturn(Optional.empty());

        // Act
        var result = service.handleAvatarUpdate(99L, file);

        // Assert
        assertTrue(result.isEmpty());
        verify(imageService, never()).upload(any(), any());
        verify(repository, never()).save(any());
    }

    // Este test verifica que handleAvatarUpdate lanza RuntimeException cuando imageService
    // lanza IOException al intentar subir el archivo. El servicio envuelve esa excepción
    // para que el controlador pueda manejarla como un error interno del servidor.
    @Test
void handleAvatarUpdate_shouldThrowRuntimeException_whenImageServiceFails() throws Exception {
    var repository = mock(ProfileRepository.class);
    var imageService = mock(ImageService.class);
    var service = new ProfileCommandServiceImpl(repository, imageService);
    var existing = buildProfile(1L, "Error User");
    var file = new MockMultipartFile("file", "bad.jpg", "image/jpeg", "data".getBytes());

    when(repository.findByUserId_UserId(1L)).thenReturn(Optional.of(existing));
    when(imageService.upload(any(byte[].class), anyString()))
            .thenThrow(new RuntimeException("Cloudinary unavailable"));

    assertThrows(RuntimeException.class, () -> service.handleAvatarUpdate(1L, file));
}
}
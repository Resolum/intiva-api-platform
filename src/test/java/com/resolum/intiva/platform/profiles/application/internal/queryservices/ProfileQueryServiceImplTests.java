package com.resolum.intiva.platform.profiles.application.internal.queryservices;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Profile;
import com.resolum.intiva.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.resolum.intiva.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests para {@link ProfileQueryServiceImpl}.
 */
class ProfileQueryServiceImplTests {

    private Profile buildProfile(Long userId, String name) {
        return Profile.builder()
                .userId(userId)
                .name(name)
                .age(25)
                .birthDate(null)
                .avatarUrl(null)
                .publicId(null)
                .build();
    }

    // Este test verifica que handle(GetProfileByUserIdQuery) retorna el perfil
    // correcto cuando existe un perfil para el userId consultado.
    // Es el flujo principal del endpoint GET /api/v1/profiles/{userId}.
    @Test
    void handle_shouldReturnProfile_whenProfileExists() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var service = new ProfileQueryServiceImpl(repository);
        var expected = buildProfile(1L, "Juan Flores");
        var query = new GetProfileByUserIdQuery(1L);

        when(repository.findByUserId_UserId(1L)).thenReturn(Optional.of(expected));

        // Act
        var result = service.handle(query);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Juan Flores", result.get().getName());
        assertEquals(1L, result.get().getUserId().userId());
    }

    // Este test verifica que handle(GetProfileByUserIdQuery) retorna Optional.empty()
    // cuando no existe un perfil registrado para ese userId.
    // El controlador convierte este resultado en una respuesta HTTP 404.
    @Test
    void handle_shouldReturnEmpty_whenProfileDoesNotExist() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var service = new ProfileQueryServiceImpl(repository);
        var query = new GetProfileByUserIdQuery(99L);

        when(repository.findByUserId_UserId(99L)).thenReturn(Optional.empty());

        // Act
        var result = service.handle(query);

        // Assert
        assertTrue(result.isEmpty());
    }

    // Este test verifica que handle llama exactamente una vez al repositorio
    // con el userId correcto extraído del query.
    // Garantiza que el servicio no realiza consultas redundantes ni usa valores incorrectos.
    @Test
    void handle_shouldCallRepositoryWithCorrectUserId() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var service = new ProfileQueryServiceImpl(repository);
        var query = new GetProfileByUserIdQuery(42L);

        when(repository.findByUserId_UserId(42L)).thenReturn(Optional.empty());

        // Act
        service.handle(query);

        // Assert
        verify(repository, times(1)).findByUserId_UserId(42L);
    }

    // Este test verifica que handle retorna el perfil con todos sus campos intactos,
    // incluyendo el avatar por defecto. El servicio no debe transformar ni truncar
    // ningún campo del aggregate antes de retornarlo.
    @Test
    void handle_shouldReturnProfileWithDefaultAvatarIntact() {
        // Arrange
        var repository = mock(ProfileRepository.class);
        var service = new ProfileQueryServiceImpl(repository);
        var profile = buildProfile(5L, "Marta Solano");
        var query = new GetProfileByUserIdQuery(5L);

        when(repository.findByUserId_UserId(5L)).thenReturn(Optional.of(profile));

        // Act
        var result = service.handle(query);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().hasDefaultAvatar());
        assertNotNull(result.get().getAvatarUrl());
    }
}
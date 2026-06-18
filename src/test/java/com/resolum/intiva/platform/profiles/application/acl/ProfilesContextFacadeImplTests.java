package com.resolum.intiva.platform.profiles.application.acl;

import com.resolum.intiva.platform.profiles.domain.model.aggregates.Profile;
import com.resolum.intiva.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.resolum.intiva.platform.profiles.domain.model.services.ProfileQueryService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests para {@link ProfilesContextFacadeImpl}.
 */
class ProfilesContextFacadeImplTests {

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

    // Este test verifica que getProfileName retorna el nombre del perfil
    // cuando existe un perfil asociado al userId solicitado.
    // El facade es consumido por otros bounded contexts, por eso debe retornar un valor limpio.
    @Test
    void getProfileName_shouldReturnProfileName_whenProfileExists() {
        // Arrange
        var queryService = mock(ProfileQueryService.class);
        var facade = new ProfilesContextFacadeImpl(queryService);
        var profile = buildProfile(1L, "Lucía Mendoza");

        when(queryService.handle(new GetProfileByUserIdQuery(1L))).thenReturn(Optional.of(profile));

        // Act
        var result = facade.getProfileName(1L);

        // Assert
        assertEquals("Lucía Mendoza", result);
    }

    // Este test verifica que getProfileName retorna una cadena vacía cuando no existe
    // un perfil para el userId. El facade nunca debe retornar null para evitar
    // NullPointerException en los bounded contexts consumidores.
    @Test
    void getProfileName_shouldReturnEmptyString_whenProfileDoesNotExist() {
        // Arrange
        var queryService = mock(ProfileQueryService.class);
        var facade = new ProfilesContextFacadeImpl(queryService);

        when(queryService.handle(new GetProfileByUserIdQuery(99L))).thenReturn(Optional.empty());

        // Act
        var result = facade.getProfileName(99L);

        // Assert
        assertEquals("", result);
        assertNotNull(result);
    }

    // Este test verifica que getProfileName delega correctamente al ProfileQueryService
    // con el query correcto construido desde el userId recibido.
    @Test
    void getProfileName_shouldDelegateToQueryService() {
        // Arrange
        var queryService = mock(ProfileQueryService.class);
        var facade = new ProfilesContextFacadeImpl(queryService);

        when(queryService.handle(new GetProfileByUserIdQuery(7L))).thenReturn(Optional.empty());

        // Act
        facade.getProfileName(7L);

        // Assert
        verify(queryService, times(1)).handle(new GetProfileByUserIdQuery(7L));
    }
}
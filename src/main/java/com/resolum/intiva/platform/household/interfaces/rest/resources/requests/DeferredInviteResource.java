package com.resolum.intiva.platform.household.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body to persist a deferred deep link invitation.")
public record DeferredInviteResource(

        @NotBlank
        @Schema(description = "Installation identifier.", example = "abc123-def456")
        String installId,

        @NotBlank
        @Schema(description = "Invitation token.", example = "550e8400-e29b-41d4-a716-446655440000")
        String token
) {
}

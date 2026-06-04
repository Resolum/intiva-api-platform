package com.resolum.intiva.platform.household.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body used to create a family group.")
public record CreateFamilyResource(
        @NotBlank
        @Size(max = 100)
        @Schema(description = "Family group name.", example = "Mi Familia")
        String name,

        @Schema(description = "Optional description of the family group.", example = "Grupo familiar principal")
        String description
) {
}

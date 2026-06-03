package com.resolum.intiva.platform.household.interfaces.rest.resources.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body used to assign a role to a family group member.")
public record AssignRoleResource(
        @NotBlank
        @Schema(description = "New role for the member.", example = "MEMBER", allowableValues = {"ADMIN", "MEMBER"})
        String role
) {
}

package com.resolum.intiva.platform.profiles.interfaces.rest.controllers;

import com.resolum.intiva.platform.profiles.application.internal.outboundservices.ProfilesExternalIamService;
import com.resolum.intiva.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.resolum.intiva.platform.profiles.domain.model.services.ProfileCommandService;
import com.resolum.intiva.platform.profiles.domain.model.services.ProfileQueryService;
import com.resolum.intiva.platform.profiles.interfaces.rest.assemblers.ProfileResourceFromEntityAssembler;
import com.resolum.intiva.platform.profiles.interfaces.rest.assemblers.UpdateProfileCommandFromResourceAssembler;
import com.resolum.intiva.platform.profiles.interfaces.rest.resources.requests.UpdateAvatarResource;
import com.resolum.intiva.platform.profiles.interfaces.rest.resources.requests.UpdateProfileResource;
import com.resolum.intiva.platform.profiles.interfaces.rest.resources.responses.ProfileResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(value = "/api/v1/profiles", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "User Profiles Management")
public class UserProfilesController {

    private final ProfileQueryService profileQueryService;
    private final ProfileCommandService profileCommandService;
    private final ProfilesExternalIamService profilesExternalIamService;

    public UserProfilesController(
            ProfileQueryService profileQueryService,
            ProfileCommandService profileCommandService,
            ProfilesExternalIamService profilesExternalIamService) {
        this.profileQueryService = profileQueryService;
        this.profileCommandService = profileCommandService;
        this.profilesExternalIamService = profilesExternalIamService;
    }

    /**
     * Retrieves a profile by its associated user identifier.
     *
     * @param userId user identifier
     * @return profile resource if found
     */
    @Operation(summary = "Get a profile by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResource> getProfileByUserId(@PathVariable Long userId) {
        var profile = profileQueryService.handle(new GetProfileByUserIdQuery(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Profile not found for user: " + userId));

        var email = profilesExternalIamService.getUserEmail(userId);
        return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile, email));
    }

    /**
     * Updates the personal information of an existing profile.
     *
     * @param userId   user identifier
     * @param resource request body containing the profile fields to update
     * @return updated profile resource
     */
    @Operation(summary = "Update profile personal information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PutMapping(value = "/{userId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResource> updateProfile(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateProfileResource resource) {
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var profile = profileCommandService.handle(command)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Profile not found for user: " + userId));

        var email = profilesExternalIamService.getUserEmail(userId);
        return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile, email));
    }

    /**
     * Updates the avatar image of a user's profile.
     *
     * @param userId   user identifier
     * @param resource request part containing the image file
     * @return updated profile resource with the new avatar URL
     */
    @Operation(summary = "Update profile avatar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatar updated"),
            @ApiResponse(responseCode = "404", description = "Profile not found"),
            @ApiResponse(responseCode = "400", description = "Invalid image format or size exceeded")
    })
    @PatchMapping(value = "/{userId}/avatar", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResource> updateProfileAvatar(
            @PathVariable Long userId,
            @ModelAttribute UpdateAvatarResource resource) {
        var profile = profileCommandService.handleAvatarUpdate(userId, resource.file())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Profile not found for user: " + userId));

        var email = profilesExternalIamService.getUserEmail(userId);
        return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile, email));
    }
}

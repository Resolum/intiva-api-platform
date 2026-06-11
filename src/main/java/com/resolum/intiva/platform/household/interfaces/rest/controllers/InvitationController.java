package com.resolum.intiva.platform.household.interfaces.rest.controllers;

import com.resolum.intiva.platform.household.domain.exceptions.ResourceNotFoundException;
import com.resolum.intiva.platform.household.domain.exceptions.UnauthorizedException;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetPendingInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.domain.services.InvitationCommandService;
import com.resolum.intiva.platform.household.domain.services.InvitationQueryService;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.AcceptInvitationCommandFromResourceAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.InvitationResourceFromEntityAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.RejectInvitationCommandFromResourceAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.SendInvitationCommandFromResourceAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.resources.requests.SendInvitationResource;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.InvitationResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing family group invitations.
 * All endpoints are scoped under /api/v1/users/{userId}/invitations.
 * The userId path variable identifies the acting user for accept/reject operations
 * and filters queries to return only that user's invitations.
 */
@RestController
@Tag(name = "Invitations", description = "Endpoints related to family group invitation management")
public class InvitationController {

    private final InvitationCommandService invitationCommandService;
    private final InvitationQueryService invitationQueryService;

    /**
     * Creates the controller with the required command and query services.
     *
     * @param invitationCommandService command service dependency
     * @param invitationQueryService   query service dependency
     */
    public InvitationController(InvitationCommandService invitationCommandService, InvitationQueryService invitationQueryService) {
        this.invitationCommandService = invitationCommandService;
        this.invitationQueryService = invitationQueryService;
    }

    /**
     * Accepts a pending invitation for the specified user.
     * Adds the user as a MEMBER of the family group and marks the invitation as ACCEPTED.
     *
     * @param userId       the numeric ID of the user accepting the invitation
     * @param invitationId the ID of the invitation to accept
     * @return 200 with the updated invitation resource, 400 if already responded or expired,
     *         403 if the user is not the invited user, 404 if not found
     */
    @PatchMapping("/api/v1/users/{userId}/invitations/{invitationId}/accept")
    @Operation(
            summary = "Accept a family group invitation",
            description = "Accepts a pending and valid invitation. Adds the user as a MEMBER of the family group."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation accepted successfully"),
            @ApiResponse(responseCode = "400", description = "Invitation already responded or expired"),
            @ApiResponse(responseCode = "403", description = "User is not the invited user"),
            @ApiResponse(responseCode = "404", description = "Invitation not found")
    })
    public ResponseEntity<?> acceptInvitation(
            @PathVariable Long userId,
            @PathVariable Long invitationId) {
        try {
            var command = AcceptInvitationCommandFromResourceAssembler.toCommandFromResource(invitationId, userId);
            var invitation = invitationCommandService.handle(command);
            var resource = InvitationResourceFromEntityAssembler.toResourceFromEntity(invitation);
            return ResponseEntity.ok(resource);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Rejects a pending invitation for the specified user.
     * Marks the invitation as REJECTED without adding the user to the family group.
     *
     * @param userId       the numeric ID of the user rejecting the invitation
     * @param invitationId the ID of the invitation to reject
     * @return 200 with the updated invitation resource, 400 if already responded or expired,
     *         403 if the user is not the invited user, 404 if not found
     */
    @PatchMapping("/api/v1/users/{userId}/invitations/{invitationId}/reject")
    @Operation(
            summary = "Reject a family group invitation",
            description = "Rejects a pending invitation. The user is not added to the family group."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invitation already responded or expired"),
            @ApiResponse(responseCode = "403", description = "User is not the invited user"),
            @ApiResponse(responseCode = "404", description = "Invitation not found")
    })
    public ResponseEntity<?> rejectInvitation(
            @PathVariable Long userId,
            @PathVariable Long invitationId) {
        try {
            var command = RejectInvitationCommandFromResourceAssembler.toCommandFromResource(invitationId, userId);
            var invitation = invitationCommandService.handle(command);
            var resource = InvitationResourceFromEntityAssembler.toResourceFromEntity(invitation);
            return ResponseEntity.ok(resource);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all pending and non-expired invitations for the specified user.
     *
     * @param userId the numeric ID of the user
     * @return 200 with the list of pending invitations
     */
    @GetMapping("/api/v1/users/{userId}/invitations/pending")
    @Operation(
            summary = "Get pending invitations for a user",
            description = "Retrieves all invitations with PENDING status that have not yet expired for the specified user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending invitations retrieved successfully")
    })
    public ResponseEntity<List<InvitationResource>> getMyPendingInvitations(@PathVariable Long userId) {
        var query = new GetPendingInvitationsByUserIdQuery(new UserId(userId));
        var invitations = invitationQueryService.handle(query);
        var resources = invitations.stream()
                .map(InvitationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves all invitations (regardless of status) for the specified user.
     *
     * @param userId the numeric ID of the user
     * @return 200 with the list of all invitations
     */
    @GetMapping("/api/v1/users/{userId}/invitations")
    @Operation(
            summary = "Get all invitations for a user",
            description = "Retrieves all invitations (PENDING, ACCEPTED, REJECTED) for the specified user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitations retrieved successfully")
    })
    public ResponseEntity<List<InvitationResource>> getMyInvitations(@PathVariable Long userId) {
        var query = new GetInvitationsByUserIdQuery(new UserId(userId));
        var invitations = invitationQueryService.handle(query);
        var resources = invitations.stream()
                .map(InvitationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PostMapping("/api/v1/users/{userId}/families/{familyId}/invitations")
    @Operation(
            summary = "Send a family group invitation",
            description = "Sends a new invitation to join the family group. If a PENDING invitation already exists for the same user, it is revoked first."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invitation sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or family cannot accept new members"),
            @ApiResponse(responseCode = "403", description = "User is not an ADMIN of this family"),
            @ApiResponse(responseCode = "404", description = "Family not found")
    })
    public ResponseEntity<?> sendInvitation(
            @PathVariable Long userId,
            @PathVariable Long familyId,
            @Valid @RequestBody SendInvitationResource resource) {
        try {
            var command = SendInvitationCommandFromResourceAssembler.toCommandFromResource(resource, familyId, userId);
            var invitation = invitationCommandService.handle(command);
            var invitationResource = InvitationResourceFromEntityAssembler.toResourceFromEntity(invitation);
            return new ResponseEntity<>(invitationResource, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

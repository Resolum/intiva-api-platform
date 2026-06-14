package com.resolum.intiva.platform.household.interfaces.rest.controllers;

import com.resolum.intiva.platform.household.application.internal.outboundservices.QrCodeGeneratorService;
import com.resolum.intiva.platform.household.domain.exceptions.InvitationAlreadyPendingException;
import com.resolum.intiva.platform.household.domain.exceptions.InvitationExpiredException;
import com.resolum.intiva.platform.household.domain.exceptions.ResourceNotFoundException;
import com.resolum.intiva.platform.household.domain.exceptions.UnauthorizedException;
import com.resolum.intiva.platform.household.domain.model.queries.GetActiveInvitationByFamilyIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationByTokenQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetPendingInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.domain.services.InvitationCommandService;
import com.resolum.intiva.platform.household.domain.services.InvitationQueryService;
import com.resolum.intiva.platform.household.infrastructure.persistence.redis.entities.DeferredDeepLinkEntity;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.DeferredDeepLinkRepository;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.AcceptInvitationCommandFromResourceAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.InvitationLinkResourceFromEntityAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.InvitationPublicInfoResourceFromEntityAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.InvitationResourceFromEntityAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.RejectInvitationCommandFromResourceAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.SendInvitationCommandFromResourceAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.SendInvitationLinkCommandFromResourceAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.resources.requests.DeferredInviteResource;
import com.resolum.intiva.platform.household.interfaces.rest.resources.requests.SendInvitationLinkResource;
import com.resolum.intiva.platform.household.interfaces.rest.resources.requests.SendInvitationResource;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.InvitationQrResource;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.InvitationResource;
import com.resolum.intiva.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing family group invitations.
 * All endpoints are scoped under /api/v1/users/{userId}/invitations.
 * The userId path variable identifies the acting user for accept/reject operations
 * and filters queries to return only that user's invitations.
 */
@Slf4j
@RestController
@Tag(name = "Invitations", description = "Endpoints related to family group invitation management")
public class InvitationController {

    private final InvitationCommandService invitationCommandService;
    private final InvitationQueryService invitationQueryService;
    private final QrCodeGeneratorService qrCodeGeneratorService;
    private final DeferredDeepLinkRepository deferredDeepLinkRepository;

    @Value("${app.invitation.base-url}")
    private String invitationBaseUrl;

    /**
     * Creates the controller with the required services.
     *
     * @param invitationCommandService   command service dependency
     * @param invitationQueryService     query service dependency
     * @param qrCodeGeneratorService     QR code generator service dependency
     * @param deferredDeepLinkRepository deferred deep link repository
     */
    public InvitationController(InvitationCommandService invitationCommandService,
                                InvitationQueryService invitationQueryService,
                                QrCodeGeneratorService qrCodeGeneratorService,
                                DeferredDeepLinkRepository deferredDeepLinkRepository) {
        this.invitationCommandService = invitationCommandService;
        this.invitationQueryService = invitationQueryService;
        this.qrCodeGeneratorService = qrCodeGeneratorService;
        this.deferredDeepLinkRepository = deferredDeepLinkRepository;
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
        log.info("Accepting invitation {} for user {}", invitationId, userId);
        try {
            var command = AcceptInvitationCommandFromResourceAssembler.toCommandFromResource(invitationId, userId);
            var invitation = invitationCommandService.handle(command);
            var resource = InvitationResourceFromEntityAssembler.toResourceFromEntity(invitation);
            log.info("Invitation {} accepted successfully by user {}", invitationId, userId);
            return ResponseEntity.ok(resource);
        } catch (ResourceNotFoundException e) {
            log.warn("Invitation {} not found for user {}", invitationId, userId);
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            log.warn("User {} unauthorized to accept invitation {}: {}", userId, invitationId, e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("Invitation {} cannot be accepted by user {}: {}", invitationId, userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Rejects a pending invitation by token.
     * The endpoint is public; if the user is authenticated, their userId is passed as a query param.
     *
     * @param token      the unique token of the invitation to reject
     * @param rejectorId the numeric ID of the user rejecting (optional, null if unauthenticated)
     * @return 200 with confirmation message, 404 if not found, 409 if already responded
     */
    @PatchMapping("/api/v1/invitations/{token}/reject")
    @Operation(
            summary = "Reject a family group invitation by token",
            description = "Rejects a pending invitation using its token. Authentication is optional."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Invitation not found"),
            @ApiResponse(responseCode = "409", description = "Invitation is not pending")
    })
    public ResponseEntity<?> rejectInvitation(
            @PathVariable String token,
            @RequestParam(value = "userId", required = false) Long rejectorId) {
        log.info("Rejecting invitation with token: {} by user: {}", token, rejectorId);

        var rejectorName = extractRejectorName();

        try {
            var command = RejectInvitationCommandFromResourceAssembler.toCommandFromResource(token, rejectorId, rejectorName);
            invitationCommandService.handle(command);
            log.info("Invitation with token {} rejected successfully", token);
            return ResponseEntity.ok(Map.of("message", "Invitation rejected"));
        } catch (ResourceNotFoundException e) {
            log.warn("Invitation not found for token: {}", token);
            return ResponseEntity.notFound().build();
        } catch (InvitationAlreadyPendingException e) {
            log.warn("Invitation with token {} is not pending: {}", token, e.getMessage());
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/v1/users/{userId}/invitations/pending")
    @Operation(
            summary = "Get pending invitations for a user",
            description = "Retrieves all invitations with PENDING status that have not yet expired for the specified user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending invitations retrieved successfully")
    })
    public ResponseEntity<List<InvitationResource>> getMyPendingInvitations(@PathVariable Long userId) {
        log.debug("Fetching pending invitations for user {}", userId);
        var query = new GetPendingInvitationsByUserIdQuery(new UserId(userId));
        var invitations = invitationQueryService.handle(query);
        var resources = invitations.stream()
                .map(InvitationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        log.debug("Found {} pending invitation(s) for user {}", resources.size(), userId);
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
        log.debug("Fetching all invitations for user {}", userId);
        var query = new GetInvitationsByUserIdQuery(new UserId(userId));
        var invitations = invitationQueryService.handle(query);
        var resources = invitations.stream()
                .map(InvitationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        log.debug("Found {} invitation(s) for user {}", resources.size(), userId);
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
        log.info("Sending invitation to family {} by user {}", familyId, userId);
        try {
            var command = SendInvitationCommandFromResourceAssembler.toCommandFromResource(resource, familyId, userId);
            var invitation = invitationCommandService.handle(command);
            var invitationResource = InvitationResourceFromEntityAssembler.toResourceFromEntity(invitation);
            log.info("Invitation sent successfully to family {} by user {}", familyId, userId);
            return new ResponseEntity<>(invitationResource, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            log.warn("Resource not found while sending invitation: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized to send invitation: {}", e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("Illegal state while sending invitation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/v1/families/{familyId}/invitations/qr")
    @Operation(
            summary = "Get QR code for an active family invitation",
            description = "Finds the active PENDING invitation for the specified family and returns its invitation link as a QR code in Base64-encoded PNG format. The invitation link is valid for 7 days."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "QR code generated successfully"),
            @ApiResponse(responseCode = "404", description = "No active invitation found for this family")
    })
    public ResponseEntity<?> getInvitationQr(@PathVariable Long familyId) {
        log.info("Generating QR code for family {}", familyId);
        try {
            var query = new GetActiveInvitationByFamilyIdQuery(familyId);
            var invitation = invitationQueryService.handle(query)
                    .orElseThrow(() -> new ResourceNotFoundException("No active invitation found for family: " + familyId));

            var link = invitationBaseUrl + "?token=" + invitation.getToken();
            var qrBase64 = qrCodeGeneratorService.generateQrBase64(link, 250, 250);

            var resource = new InvitationQrResource(
                    invitation.getToken(),
                    qrBase64,
                    link,
                    invitation.getExpiresAt().toString()
            );

            log.info("QR code generated for family {} invitation token {}", familyId, invitation.getToken());
            return ResponseEntity.ok(resource);
        } catch (ResourceNotFoundException e) {
            log.warn("No active invitation found for family {}", familyId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/v1/users/{userId}/invitations/link")
    @Operation(
            summary = "Send a family group invitation link",
            description = "Creates a link-based invitation for the specified family group. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Invitation link created successfully"),
            @ApiResponse(responseCode = "403", description = "User is not an ADMIN of this family"),
            @ApiResponse(responseCode = "404", description = "Family not found")
    })
    public ResponseEntity<?> sendInvitationLink(
            @PathVariable Long userId,
            @Valid @RequestBody SendInvitationLinkResource resource) {
        log.info("Sending invitation link for family {} by user {} with email {}",
                resource.familyId(), userId, resource.inviteeEmail());
        try {
            var command = SendInvitationLinkCommandFromResourceAssembler.toCommandFromResource(resource, userId);
            var result = invitationCommandService.sendInvitationLink(command);
            var linkResource = InvitationLinkResourceFromEntityAssembler.toResourceFromResult(result);
            log.info("Invitation link created for family {} with token {}", resource.familyId(), result.token());
            return new ResponseEntity<>(linkResource, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            log.warn("Resource not found while sending invitation link: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized to send invitation link: {}", e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("Illegal state while sending invitation link: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/v1/invitations/public/{token}")
    @Operation(
            summary = "Get public invitation information by token",
            description = "Retrieves public information about an invitation using its token. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation found"),
            @ApiResponse(responseCode = "404", description = "Invitation not found"),
            @ApiResponse(responseCode = "410", description = "Invitation has expired"),
            @ApiResponse(responseCode = "409", description = "Invitation has already been used")
    })
    public ResponseEntity<?> getInvitationByToken(@PathVariable String token) {
        log.info("Getting public invitation info for token: {}", token);
        try {
            var query = new GetInvitationByTokenQuery(token);
            var info = invitationQueryService.getInvitationByToken(query);
            var resource = InvitationPublicInfoResourceFromEntityAssembler.toResourceFromInfo(info);
            log.info("Public invitation info retrieved for token: {}", token);
            return ResponseEntity.ok(resource);
        } catch (ResourceNotFoundException e) {
            log.warn("Invitation not found for token: {}", token);
            return ResponseEntity.notFound().build();
        } catch (InvitationExpiredException e) {
            log.warn("Invitation expired for token: {}", token);
            return ResponseEntity.status(410).body(Map.of("error", e.getMessage()));
        } catch (InvitationAlreadyPendingException e) {
            log.warn("Invitation already responded for token: {}", token);
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/v1/invitations/deferred")
    @Operation(
            summary = "Persist a deferred deep link invitation",
            description = "Stores a deferred deep link for later claiming. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deferred deep link persisted successfully")
    })
    public ResponseEntity<?> saveDeferredInvite(@Valid @RequestBody DeferredInviteResource resource) {
        log.info("Saving deferred deep link for installId: {}, token: {}", resource.installId(), resource.token());
        var entity = new DeferredDeepLinkEntity(resource.installId(), resource.token());
        deferredDeepLinkRepository.save(entity);
        log.info("Deferred deep link saved for installId: {}", resource.installId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/api/v1/invitations/deferred")
    @Operation(
            summary = "Get deferred deep link by installId",
            description = "Retrieves and claims a pending deferred deep link for the given installId. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deferred deep link found and claimed"),
            @ApiResponse(responseCode = "204", description = "No deferred deep link found for this installId")
    })
    public ResponseEntity<?> getDeferredInvite(@RequestParam String installId) {
        log.info("Getting deferred deep link for installId: {}", installId);
        var deferredLink = deferredDeepLinkRepository.findByInstallIdAndClaimedFalse(installId);
        if (deferredLink.isPresent()) {
            var link = deferredLink.get();
            var token = link.getInviteToken();

            try {
                var query = new GetInvitationByTokenQuery(token);
                var info = invitationQueryService.getInvitationByToken(query);

                link.markClaimed();
                deferredDeepLinkRepository.save(link);

                log.info("Deferred deep link claimed for installId: {}", installId);
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "groupName", info.groupName(),
                        "inviterName", info.inviterName()
                ));
            } catch (ResourceNotFoundException | InvitationExpiredException | InvitationAlreadyPendingException e) {
                log.warn("Invitation for deferred link is no longer valid: {}", e.getMessage());
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "groupName", "",
                        "inviterName", ""
                ));
            }
        }
        log.info("No deferred deep link found for installId: {}", installId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts the display name of the authenticated user from the security context.
     *
     * @return the username (email) from the JWT, or null if not authenticated
     */
    private String extractRejectorName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }
}

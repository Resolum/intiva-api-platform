package com.resolum.intiva.platform.household.interfaces.rest.controllers;

import com.resolum.intiva.platform.household.domain.exceptions.ResourceNotFoundException;
import com.resolum.intiva.platform.household.domain.exceptions.UnauthorizedException;
import com.resolum.intiva.platform.household.domain.model.queries.GetMemberByIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetMembersByFamilyIdQuery;
import com.resolum.intiva.platform.household.domain.services.FamilyMemberCommandService;
import com.resolum.intiva.platform.household.domain.services.FamilyMemberQueryService;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.AssignRoleCommandFromResourceAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.FamilyMemberResourceFromEntityAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.assemblers.FamilyMembersListResourceFromEntityAssembler;
import com.resolum.intiva.platform.household.interfaces.rest.resources.requests.AssignRoleResource;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.FamilyMemberResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST controller for managing family group members.
 * Exposes endpoints for listing, retrieving, and assigning roles to family members.
 */
@RestController
@RequestMapping(value = "/api/v1/families/{familyId}/members", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Family Members", description = "Endpoints related to family group members management")
public class FamilyMemberController {

    private final FamilyMemberCommandService familyMemberCommandService;
    private final FamilyMemberQueryService familyMemberQueryService;

    /**
     * Creates the controller with the required command and query services.
     *
     * @param familyMemberCommandService command service dependency
     * @param familyMemberQueryService   query service dependency
     */
    public FamilyMemberController(FamilyMemberCommandService familyMemberCommandService, FamilyMemberQueryService familyMemberQueryService) {
        this.familyMemberCommandService = familyMemberCommandService;
        this.familyMemberQueryService = familyMemberQueryService;
    }

    /**
     * Retrieves all active members of the specified family group.
     * The authenticated user must belong to the group.
     *
     * @param familyId  the ID of the family group
     * @param principal the authenticated user; their numeric ID is used for membership validation
     * @return 200 with the member list resource, or 403 if the user does not belong to the group
     */
    @GetMapping
    @Operation(
            summary = "Get family group members",
            description = "Retrieves all active members of a family group. The authenticated user must belong to the group."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not belong to this family group")
    })
    public ResponseEntity<?> getMembers(
            @PathVariable Long familyId,
            Principal principal) {
        try {
            var requesterId = new UserId(Long.parseLong(principal.getName()));
            var query = new GetMembersByFamilyIdQuery(familyId, requesterId);
            var members = familyMemberQueryService.handle(query);
            var resource = FamilyMembersListResourceFromEntityAssembler.toResourceFromEntityList(familyId, members);
            return ResponseEntity.ok(resource);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    /**
     * Retrieves a specific family member by their ID within the given family group.
     *
     * @param familyId the ID of the family group
     * @param memberId the ID of the member to retrieve
     * @return 200 with the member resource, or 404 if not found
     */
    @GetMapping("/{memberId}")
    @Operation(
            summary = "Get family member by ID",
            description = "Retrieves a specific member of a family group by their member identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found"),
            @ApiResponse(responseCode = "404", description = "Member not found in this family group")
    })
    public ResponseEntity<FamilyMemberResource> getMember(
            @PathVariable Long familyId,
            @PathVariable Long memberId) {
        var query = new GetMemberByIdQuery(memberId, familyId);
        var member = familyMemberQueryService.handle(query);
        return member
                .map(m -> ResponseEntity.ok(FamilyMemberResourceFromEntityAssembler.toResourceFromEntity(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Assigns a new role to a member of the family group.
     * Only ADMIN members can assign roles. The group must always keep at least one ADMIN.
     *
     * @param familyId  the ID of the family group
     * @param memberId  the ID of the target member
     * @param resource  the new role to assign
     * @param principal the authenticated user performing the role assignment (must be ADMIN)
     * @return 200 with the updated member resource, 400 if the last ADMIN would be demoted,
     *         403 if requester is not ADMIN, 404 if member or family not found
     */
    @PatchMapping("/{memberId}/role")
    @Operation(
            summary = "Assign role to a family group member",
            description = "Assigns ADMIN or MEMBER role to a member. Only ADMIN members can perform this action. The group must retain at least one ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot demote the last ADMIN of the group"),
            @ApiResponse(responseCode = "403", description = "Only ADMIN members can assign roles"),
            @ApiResponse(responseCode = "404", description = "Family group or member not found")
    })
    public ResponseEntity<?> assignRole(
            @PathVariable Long familyId,
            @PathVariable Long memberId,
            @Valid @RequestBody AssignRoleResource resource,
            Principal principal) {
        try {
            var requesterId = Long.parseLong(principal.getName());
            var command = AssignRoleCommandFromResourceAssembler.toCommandFromResource(familyId, memberId, resource.role(), requesterId);
            var member = familyMemberCommandService.handle(command);
            var memberResource = FamilyMemberResourceFromEntityAssembler.toResourceFromEntity(member);
            return ResponseEntity.ok(memberResource);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

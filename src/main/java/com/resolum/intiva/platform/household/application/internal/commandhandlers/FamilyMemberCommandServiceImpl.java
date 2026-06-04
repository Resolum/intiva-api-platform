package com.resolum.intiva.platform.household.application.internal.commandhandlers;

import com.resolum.intiva.platform.household.domain.exceptions.ResourceNotFoundException;
import com.resolum.intiva.platform.household.domain.exceptions.UnauthorizedException;
import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.domain.model.commands.AssignRoleCommand;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyMemberStatus;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyRole;
import com.resolum.intiva.platform.household.domain.services.FamilyMemberCommandService;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyMemberRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of FamilyMemberCommandService that handles role assignment.
 */
@Service
public class FamilyMemberCommandServiceImpl implements FamilyMemberCommandService {

    private final Logger LOGGER = LoggerFactory.getLogger(FamilyMemberCommandServiceImpl.class);

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;

    /**
     * Creates the command service with the required repository dependencies.
     *
     * @param familyRepository       the family group repository
     * @param familyMemberRepository the family member repository
     */
    public FamilyMemberCommandServiceImpl(FamilyRepository familyRepository, FamilyMemberRepository familyMemberRepository) {
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
    }

    /**
     * Assigns a new role to a family member.
     * Validates that the family exists, the requester is an ADMIN,
     * the target member belongs to the group, and at least one ADMIN remains.
     *
     * @param command the role assignment command
     * @return the updated FamilyMember with the new role
     * @throws ResourceNotFoundException if the family or member is not found
     * @throws UnauthorizedException     if the requester is not an ADMIN
     * @throws IllegalStateException     if demoting the last ADMIN
     */
    @Override
    @Transactional
    public FamilyMember handle(AssignRoleCommand command) {
        familyRepository.findById(command.familyId())
                .orElseThrow(() -> new ResourceNotFoundException("Family not found with id: " + command.familyId()));

        var requesterMember = familyMemberRepository.findByFamilyIdAndUserId(command.familyId(), command.requesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Requester is not a member of this family"));

        if (requesterMember.getRole() != FamilyRole.ADMIN) {
            throw new UnauthorizedException("Only ADMIN can assign roles");
        }

        var targetMember = familyMemberRepository.findById(command.targetMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + command.targetMemberId()));

        if (!targetMember.getFamilyId().equals(command.familyId())) {
            throw new ResourceNotFoundException("Member does not belong to this family");
        }

        if (command.newRole() == FamilyRole.MEMBER && targetMember.getRole() == FamilyRole.ADMIN) {
            long adminCount = familyMemberRepository.countByFamilyIdAndRoleAndStatus(
                    command.familyId(), FamilyRole.ADMIN, FamilyMemberStatus.ACTIVE);
            if (adminCount <= 1) {
                throw new IllegalStateException("Group must have at least one ADMIN");
            }
        }

        targetMember.asignRole(command.newRole());
        var savedMember = familyMemberRepository.save(targetMember);

        LOGGER.info("Role {} assigned to member {} in family {} by user {}",
                command.newRole(), command.targetMemberId(), command.familyId(), command.requesterId().getValue());

        return savedMember;
    }
}

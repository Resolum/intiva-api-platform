package com.resolum.intiva.platform.household.application.internal.commandhandlers;

import com.resolum.intiva.platform.household.domain.exceptions.ResourceNotFoundException;
import com.resolum.intiva.platform.household.domain.exceptions.UnauthorizedException;
import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.domain.model.commands.AcceptInvitationCommand;
import com.resolum.intiva.platform.household.domain.model.commands.RejectInvitationCommand;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyRole;
import com.resolum.intiva.platform.household.domain.services.InvitationCommandService;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyMemberRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.InvitationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of InvitationCommandService that handles accept and reject commands.
 */
@Service
public class InvitationCommandServiceImpl implements InvitationCommandService {

    private final Logger LOGGER = LoggerFactory.getLogger(InvitationCommandServiceImpl.class);

    private final InvitationRepository invitationRepository;
    private final FamilyMemberRepository familyMemberRepository;

    /**
     * Creates the command service with the required repository dependencies.
     *
     * @param invitationRepository   the invitation repository
     * @param familyMemberRepository the family member repository
     */
    public InvitationCommandServiceImpl(InvitationRepository invitationRepository, FamilyMemberRepository familyMemberRepository) {
        this.invitationRepository = invitationRepository;
        this.familyMemberRepository = familyMemberRepository;
    }

    /**
     * Accepts a pending invitation, adds the user as a MEMBER of the family group,
     * and marks the invitation as ACCEPTED.
     *
     * @param command the accept invitation command
     * @return the updated Invitation with ACCEPTED status
     * @throws ResourceNotFoundException if the invitation does not exist
     * @throws UnauthorizedException     if the user is not the invited user
     * @throws IllegalStateException     if the invitation has already been responded to or has expired
     */
    @Override
    @Transactional
    public Invitation handle(AcceptInvitationCommand command) {
        var invitation = invitationRepository.findById(command.invitationId())
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found with id: " + command.invitationId()));

        if (!invitation.getUserInvitedId().equals(command.userId())) {
            throw new UnauthorizedException("User is not the invited user for this invitation");
        }

        invitation.accepts();

        var member = new FamilyMember(invitation.getInvitedForFamily(), invitation.getUserInvitedId(), FamilyRole.MEMBER);
        familyMemberRepository.save(member);
        invitationRepository.save(invitation);

        LOGGER.info("Invitation {} accepted by user {}", invitation.getId(), command.userId().getValue());

        return invitation;
    }

    /**
     * Rejects a pending invitation and marks it as REJECTED.
     *
     * @param command the reject invitation command
     * @return the updated Invitation with REJECTED status
     * @throws ResourceNotFoundException if the invitation does not exist
     * @throws UnauthorizedException     if the user is not the invited user
     * @throws IllegalStateException     if the invitation has already been responded to or has expired
     */
    @Override
    @Transactional
    public Invitation handle(RejectInvitationCommand command) {
        var invitation = invitationRepository.findById(command.invitationId())
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found with id: " + command.invitationId()));

        if (!invitation.getUserInvitedId().equals(command.userId())) {
            throw new UnauthorizedException("User is not the invited user for this invitation");
        }

        invitation.rejects();
        invitationRepository.save(invitation);

        LOGGER.info("Invitation {} rejected by user {}", invitation.getId(), command.userId().getValue());

        return invitation;
    }
}

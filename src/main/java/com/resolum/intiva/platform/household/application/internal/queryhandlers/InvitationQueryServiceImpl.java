package com.resolum.intiva.platform.household.application.internal.queryhandlers;

import com.resolum.intiva.platform.household.application.internal.InvitationPublicInfo;
import com.resolum.intiva.platform.household.domain.exceptions.InvitationAlreadyPendingException;
import com.resolum.intiva.platform.household.domain.exceptions.InvitationExpiredException;
import com.resolum.intiva.platform.household.domain.exceptions.ResourceNotFoundException;
import com.resolum.intiva.platform.household.domain.model.aggregates.Family;
import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.domain.model.queries.GetActiveInvitationByFamilyIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationByIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationByTokenQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetPendingInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyMemberStatus;
import com.resolum.intiva.platform.household.domain.model.valueobjects.InvitationStatus;
import com.resolum.intiva.platform.household.domain.services.InvitationQueryService;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyMemberRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.InvitationRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.redis.repositories.InvitationLinkCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of InvitationQueryService that delegates to the JPA repository.
 */
@Slf4j
@Service
public class InvitationQueryServiceImpl implements InvitationQueryService {

    private final InvitationRepository invitationRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final InvitationLinkCacheRepository invitationLinkCacheRepository;

    /**
     * Creates the query service with the required repository dependencies.
     *
     * @param invitationRepository          the invitation repository
     * @param familyRepository              the family repository
     * @param familyMemberRepository        the family member repository
     * @param invitationLinkCacheRepository the Redis cache repository for invitation links
     */
    public InvitationQueryServiceImpl(InvitationRepository invitationRepository,
                                      FamilyRepository familyRepository,
                                      FamilyMemberRepository familyMemberRepository,
                                      InvitationLinkCacheRepository invitationLinkCacheRepository) {
        this.invitationRepository = invitationRepository;
        this.familyRepository = familyRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.invitationLinkCacheRepository = invitationLinkCacheRepository;
    }

    @Override
    public Optional<Invitation> handle(GetInvitationByIdQuery query) {
        log.debug("Querying invitation by id: {}", query.invitationId());
        return invitationRepository.findById(query.invitationId());
    }

    @Override
    public List<Invitation> handle(GetInvitationsByUserIdQuery query) {
        log.debug("Querying invitations for userId: {}", query.userId().getValue());
        return invitationRepository.findByUserInvitedId(query.userId());
    }

    @Override
    public List<Invitation> handle(GetPendingInvitationsByUserIdQuery query) {
        log.debug("Querying pending invitations for userId: {}", query.userId().getValue());
        return invitationRepository.findByUserInvitedIdAndStatusAndExpiresAtAfter(
                query.userId(), InvitationStatus.PENDING, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invitation> handle(GetActiveInvitationByFamilyIdQuery query) {
        log.debug("Querying active invitation for familyId: {}", query.familyId());
        return invitationRepository.findByInvitedForFamilyAndStatus(query.familyId(), InvitationStatus.PENDING)
                .stream()
                .filter(inv -> !inv.isExpired())
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public InvitationPublicInfo getInvitationByToken(GetInvitationByTokenQuery query) {
        log.debug("Querying invitation by token: {}", query.token());

        // Check Redis cache first for link-based invitations (15-min TTL)
        var cacheOpt = invitationLinkCacheRepository.findById(query.token());
        if (cacheOpt.isPresent()) {
            var cache = cacheOpt.get();
            var family = familyRepository.findById(cache.getFamilyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Family not found for id: " + cache.getFamilyId()));

            var activeMembers = familyMemberRepository.findByFamilyIdAndStatus(
                    cache.getFamilyId(), FamilyMemberStatus.ACTIVE);
            var inviterName = String.valueOf(cache.getInviterId());

            var jpaInvitation = invitationRepository.findByToken(query.token());
            var invitationId = jpaInvitation.map(Invitation::getId).orElse(null);

            return new InvitationPublicInfo(
                    cache.getFamilyName(),
                    inviterName,
                    activeMembers.size(),
                    InvitationStatus.PENDING.name(),
                    LocalDateTime.now().plusMinutes(15),
                    cache.getFamilyId(),
                    cache.getInviterId(),
                    invitationId
            );
        }

        // Fallback to JPA for DIRECT invitations (7-day expiry)
        var invitation = invitationRepository.findByToken(query.token())
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found for token: " + query.token()));

        if (invitation.getExpiresAt() != null && LocalDateTime.now().isAfter(invitation.getExpiresAt())) {
            throw new InvitationExpiredException("Invitation has expired for token: " + query.token());
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationAlreadyPendingException("Invitation has already been responded for token: " + query.token());
        }

        var family = familyRepository.findById(invitation.getInvitedForFamily())
                .orElseThrow(() -> new ResourceNotFoundException("Family not found for id: " + invitation.getInvitedForFamily()));

        var inviterMember = familyMemberRepository.findByFamilyIdAndUserId(
                invitation.getInvitedForFamily(), invitation.getInvitedBy());

        var activeMembers = familyMemberRepository.findByFamilyIdAndStatus(
                invitation.getInvitedForFamily(), FamilyMemberStatus.ACTIVE);

        return new InvitationPublicInfo(
                family.getName(),
                inviterMember.map(FamilyMember::getUserId).map(id -> String.valueOf(id.getValue())).orElse("Unknown"),
                activeMembers.size(),
                invitation.getStatus().name(),
                invitation.getExpiresAt(),
                invitation.getInvitedForFamily(),
                invitation.getInvitedBy().getValue(),
                invitation.getId()
        );
    }
}

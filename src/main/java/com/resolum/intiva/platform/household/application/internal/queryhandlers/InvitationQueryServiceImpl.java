package com.resolum.intiva.platform.household.application.internal.queryhandlers;

import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.domain.model.queries.GetActiveInvitationByFamilyIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationByIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetPendingInvitationsByUserIdQuery;
import com.resolum.intiva.platform.household.domain.model.valueobjects.InvitationStatus;
import com.resolum.intiva.platform.household.domain.services.InvitationQueryService;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.InvitationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of InvitationQueryService that delegates to the JPA repository.
 */
@Service
public class InvitationQueryServiceImpl implements InvitationQueryService {

    private final InvitationRepository invitationRepository;

    /**
     * Creates the query service with the required repository dependency.
     *
     * @param invitationRepository the invitation repository
     */
    public InvitationQueryServiceImpl(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @Override
    public Optional<Invitation> handle(GetInvitationByIdQuery query) {
        return invitationRepository.findById(query.invitationId());
    }

    @Override
    public List<Invitation> handle(GetInvitationsByUserIdQuery query) {
        return invitationRepository.findByUserInvitedId(query.userId());
    }

    @Override
    public List<Invitation> handle(GetPendingInvitationsByUserIdQuery query) {
        return invitationRepository.findByUserInvitedIdAndStatusAndExpiresAtAfter(
                query.userId(), InvitationStatus.PENDING, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invitation> handle(GetActiveInvitationByFamilyIdQuery query) {
        return invitationRepository.findByInvitedForFamilyAndStatus(query.familyId(), InvitationStatus.PENDING)
                .stream()
                .filter(inv -> !inv.isExpired())
                .findFirst();
    }
}

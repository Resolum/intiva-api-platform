package com.resolum.intiva.platform.household.application.internal.queryhandlers;

import com.resolum.intiva.platform.household.domain.exceptions.UnauthorizedException;
import com.resolum.intiva.platform.household.domain.model.aggregates.FamilyMember;
import com.resolum.intiva.platform.household.domain.model.queries.GetMemberByIdQuery;
import com.resolum.intiva.platform.household.domain.model.queries.GetMembersByFamilyIdQuery;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyMemberStatus;
import com.resolum.intiva.platform.household.domain.services.FamilyMemberQueryService;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of FamilyMemberQueryService that delegates to the JPA repository.
 */
@Slf4j
@Service
public class FamilyMemberQueryServiceImpl implements FamilyMemberQueryService {

    private final FamilyMemberRepository familyMemberRepository;

    /**
     * Creates the query service with the required repository dependency.
     *
     * @param familyMemberRepository the family member repository
     */
    public FamilyMemberQueryServiceImpl(FamilyMemberRepository familyMemberRepository) {
        this.familyMemberRepository = familyMemberRepository;
    }

    @Override
    public List<FamilyMember> handle(GetMembersByFamilyIdQuery query) {
        log.debug("Querying members for familyId: {} by requesterId: {}", query.familyId(), query.requesterId().getValue());

        familyMemberRepository.findByFamilyIdAndUserId(query.familyId(), query.requesterId())
                .orElseThrow(() -> new UnauthorizedException("User does not belong to this family"));

        return familyMemberRepository.findByFamilyIdAndStatus(query.familyId(), FamilyMemberStatus.ACTIVE);
    }

    @Override
    public Optional<FamilyMember> handle(GetMemberByIdQuery query) {
        log.debug("Querying member by id: {} in familyId: {}", query.memberId(), query.familyId());
        return familyMemberRepository.findByIdAndFamilyId(query.memberId(), query.familyId());
    }
}

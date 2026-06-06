package com.resolum.intiva.platform.household.application.acl.services;

import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyMemberStatus;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyMemberRepository;
import com.resolum.intiva.platform.household.interfaces.acl.HouseholdContextFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default ACL facade implementation that lets external bounded contexts access household data.
 */
@Service
@Slf4j
public class HouseholdContextFacadeImpl implements HouseholdContextFacade {

    private final FamilyMemberRepository familyMemberRepository;

    public HouseholdContextFacadeImpl(FamilyMemberRepository familyMemberRepository) {
        this.familyMemberRepository = familyMemberRepository;
    }

    @Override
    public List<Long> getActiveFamilyMemberUserIds(Long familyId) {
        log.info("Querying active member user IDs for familyId={}", familyId);
        return familyMemberRepository.findByFamilyIdAndStatus(familyId, FamilyMemberStatus.ACTIVE)
                .stream()
                .map(member -> member.getUserId().getValue())
                .toList();
    }
}

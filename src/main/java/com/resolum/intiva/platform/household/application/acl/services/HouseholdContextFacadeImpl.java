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

    /**
     * Constructs the facade with the required repository dependency.
     *
     * @param familyMemberRepository the family member repository
     */
    public HouseholdContextFacadeImpl(FamilyMemberRepository familyMemberRepository) {
        this.familyMemberRepository = familyMemberRepository;
    }

    @Override
    public List<Long> getActiveFamilyMemberUserIds(Long familyId) {
        log.debug("Querying active member user IDs for familyId={}", familyId);
        var userIds = familyMemberRepository.findByFamilyIdAndStatus(familyId, FamilyMemberStatus.ACTIVE)
                .stream()
                .map(member -> member.getUserId().getValue())
                .toList();
        log.info("Retrieved {} active member(s) for familyId={}", userIds.size(), familyId);
        return userIds;
    }
}

package com.resolum.intiva.platform.communications.application.internal.outboundservices.acl;

import com.resolum.intiva.platform.household.interfaces.acl.HouseholdContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ACL service that lets the communications bounded context request household data.
 */
@Service
public class CommunicationsExternalHouseholdService {

    private final HouseholdContextFacade householdContextFacade;

    public CommunicationsExternalHouseholdService(HouseholdContextFacade householdContextFacade) {
        this.householdContextFacade = householdContextFacade;
    }

    /**
     * Retrieves the user IDs of all active members of a family group.
     *
     * @param familyId the family group identifier
     * @return list of user IDs of active family members
     */
    public List<Long> getActiveFamilyMemberUserIds(Long familyId) {
        return householdContextFacade.getActiveFamilyMemberUserIds(familyId);
    }
}

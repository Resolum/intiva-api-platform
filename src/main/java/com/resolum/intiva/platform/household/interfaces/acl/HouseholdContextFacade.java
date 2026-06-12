package com.resolum.intiva.platform.household.interfaces.acl;

import java.util.List;

/**
 * ACL facade that exposes household capabilities to other bounded contexts.
 */
public interface HouseholdContextFacade {

    /**
     * Retrieves the user IDs of all active members of a family group.
     *
     * @param familyId the family group identifier
     * @return list of user IDs of active family members
     */
    List<Long> getActiveFamilyMemberUserIds(Long familyId);
}

package com.resolum.intiva.platform.analytics.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

/**
 * Query to retrieve saving goal analytics for a given owner.
 *
 * @param ownerId   owner identifier (user id or family/group id)
 * @param ownerType owner scope (INDIVIDUAL or FAMILY)
 */
public record GetSavingGoalAnalyticsByOwnerQuery(
        String ownerId,
        OwnerTypes ownerType
) {
}

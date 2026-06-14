package com.resolum.intiva.platform.analytics.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;

/**
 * Query to retrieve spending limit analytics for a given owner.
 *
 * @param ownerId    owner identifier (user id or family/group id)
 * @param ownerType  owner scope (INDIVIDUAL or FAMILY)
 * @param periodType period granularity for which the analytics are computed
 */
public record GetSpendingLimitAnalyticsByOwnerQuery(
        String ownerId,
        OwnerTypes ownerType,
        PeriodTypes periodType
) {
}

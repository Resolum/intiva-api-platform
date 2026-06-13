package com.resolum.intiva.platform.analytics.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;

/**
 * Query to retrieve the income vs expense trend for the last N periods.
 *
 * @param ownerId       owner identifier (user id or family/group id)
 * @param ownerType     owner scope (INDIVIDUAL or FAMILY)
 * @param periodType    period granularity for each trend data point
 * @param lastNPeriods  number of past periods to include (defaults to 6)
 */
public record GetIncomeVsExpenseTrendQuery(
        String ownerId,
        OwnerTypes ownerType,
        PeriodTypes periodType,
        Integer lastNPeriods
) {
    public GetIncomeVsExpenseTrendQuery {
        if (lastNPeriods == null) lastNPeriods = 6;
    }
}

package com.resolum.intiva.platform.analytics.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;

import java.time.LocalDate;

/**
 * Query to retrieve a financial summary for a given owner and time period.
 *
 * @param ownerId     owner identifier (user id or family/group id)
 * @param ownerType   owner scope (INDIVIDUAL or FAMILY)
 * @param periodType  period granularity (DAILY, WEEKLY, MONTHLY, ANNUAL)
 * @param periodStart inclusive start date of the analysis period
 * @param periodEnd   inclusive end date of the analysis period
 */
public record GetAnalyticsSummaryByOwnerQuery(
        String ownerId,
        OwnerTypes ownerType,
        PeriodTypes periodType,
        LocalDate periodStart,
        LocalDate periodEnd
) {
}

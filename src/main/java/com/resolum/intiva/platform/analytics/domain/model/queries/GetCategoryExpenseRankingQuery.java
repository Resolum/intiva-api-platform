package com.resolum.intiva.platform.analytics.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.PeriodTypes;

import java.time.LocalDate;

/**
 * Query to retrieve the top N categories with the highest expenses for a given owner and period.
 *
 * @param ownerId     owner identifier (user id or family/group id)
 * @param ownerType   owner scope (INDIVIDUAL or FAMILY)
 * @param periodType  period granularity
 * @param periodStart inclusive start date of the analysis period
 * @param periodEnd   inclusive end date of the analysis period
 * @param limit       maximum number of categories to return (defaults to 5)
 */
public record GetCategoryExpenseRankingQuery(
        String ownerId,
        OwnerTypes ownerType,
        PeriodTypes periodType,
        LocalDate periodStart,
        LocalDate periodEnd,
        Integer limit
) {
    public GetCategoryExpenseRankingQuery {
        if (limit == null) limit = 5;
    }
}

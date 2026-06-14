package com.resolum.intiva.platform.analytics.domain.model.valueobjects;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

import java.time.LocalDate;

/**
 * Filter parameters used to scope a report generation request.
 *
 * <p>Defines the owner, time period, optional category filter, and output format
 * that together determine which transactions are included in the report and how
 * the result is rendered.</p>
 *
 * @param ownerId     the unique identifier of the report owner
 * @param ownerType   the scope type of the owner ({@link OwnerTypes#INDIVIDUAL} or {@link OwnerTypes#FAMILY})
 * @param periodStart the inclusive start date of the analysis period
 * @param periodEnd   the inclusive end date of the analysis period
 * @param categoryId  optional category identifier; when non-blank only transactions
 *                    belonging to this category are included
 * @param format      the desired output format ({@link ReportFormat#CSV} or {@link ReportFormat#PDF})
 */
public record ReportFilter(
        String ownerId,
        OwnerTypes ownerType,
        LocalDate periodStart,
        LocalDate periodEnd,
        String categoryId,
        ReportFormat format
) {
}

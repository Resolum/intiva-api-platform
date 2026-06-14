package com.resolum.intiva.platform.analytics.domain.model.queries;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

import java.time.LocalDate;

/**
 * Query to retrieve a financial report preview for a given owner and time period.
 *
 * <p>The preview aggregates income and expense totals, computes the net balance,
 * and returns the top 5 expense categories. Unlike the full report generation,
 * the preview does not produce a downloadable file — it returns the summary as a
 * JSON response.</p>
 *
 * @param ownerId     the unique identifier of the report owner
 * @param ownerType   the scope type of the owner (INDIVIDUAL or FAMILY)
 * @param periodStart the inclusive start date of the analysis period
 * @param periodEnd   the inclusive end date of the analysis period
 * @param categoryId  optional category identifier; when non-null only transactions
 *                    belonging to this category are included in the preview
 */
public record GetReportPreviewQuery(
        String ownerId,
        OwnerTypes ownerType,
        LocalDate periodStart,
        LocalDate periodEnd,
        String categoryId
) {
}

package com.resolum.intiva.platform.analytics.domain.model.commands;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportFormat;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

import java.time.LocalDate;

/**
 * Command to generate a downloadable report file (CSV or PDF) for a given owner
 * and time period.
 *
 * <p>When processed by the {@link com.resolum.intiva.platform.analytics.domain.services.ReportCommandService
 * ReportCommandService}, this command fetches the matching transactions through the
 * ACL layer, renders them into the requested format, and returns an
 * {@link com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsReport
 * AnalyticsReport} containing the binary content and file metadata.</p>
 *
 * @param ownerId     the unique identifier of the report owner
 * @param ownerType   the scope type of the owner (INDIVIDUAL or FAMILY)
 * @param periodStart the inclusive start date of the analysis period
 * @param periodEnd   the inclusive end date of the analysis period
 * @param categoryId  optional category identifier; when non-blank only transactions
 *                    belonging to this category are included
 * @param format      the desired output format (CSV or PDF)
 */
public record GenerateReportCommand(
        String ownerId,
        OwnerTypes ownerType,
        LocalDate periodStart,
        LocalDate periodEnd,
        String categoryId,
        ReportFormat format
) {
}

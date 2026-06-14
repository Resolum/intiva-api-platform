package com.resolum.intiva.platform.analytics.interfaces.rest.assemblers;

import com.resolum.intiva.platform.analytics.domain.model.commands.GenerateReportCommand;
import com.resolum.intiva.platform.analytics.domain.model.exceptions.InvalidReportFormatException;
import com.resolum.intiva.platform.analytics.domain.model.exceptions.InvalidReportPeriodException;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportFormat;
import com.resolum.intiva.platform.analytics.interfaces.rest.resources.requests.GenerateReportResource;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;

import java.time.LocalDate;

/**
 * Maps a {@link GenerateReportResource} REST request into a
 * {@link GenerateReportCommand} domain command.
 *
 * <p>This assembler performs input validation during the mapping process:
 * <ul>
 *   <li>Parses and validates the period dates (start must be before end).</li>
 *   <li>Parses the owner type string into the {@link OwnerTypes} enum.</li>
 *   <li>Parses the format string into the {@link ReportFormat} enum.</li>
 * </ul></p>
 */
public class GenerateReportCommandFromResourceAssembler {

    /**
     * Converts an API request resource into a domain command, performing validation.
     *
     * @param resource the REST request containing report generation parameters
     * @return a fully validated {@link GenerateReportCommand}
     * @throws InvalidReportPeriodException if {@code periodStart} is after {@code periodEnd}
     * @throws IllegalArgumentException      if the owner type string is not a valid
     *                                       {@link OwnerTypes} value
     * @throws InvalidReportFormatException  if the format string is not a valid
     *                                       {@link ReportFormat} value
     */
    public static GenerateReportCommand toCommandFromResource(GenerateReportResource resource) {
        var periodStart = LocalDate.parse(resource.periodStart());
        var periodEnd = LocalDate.parse(resource.periodEnd());

        if (periodStart.isAfter(periodEnd)) {
            throw new InvalidReportPeriodException("Period start must be before period end");
        }

        OwnerTypes ownerType;
        try {
            ownerType = OwnerTypes.valueOf(resource.ownerType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid owner type: " + resource.ownerType());
        }

        ReportFormat format;
        try {
            format = ReportFormat.valueOf(resource.format().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidReportFormatException("Format must be CSV or PDF");
        }

        return new GenerateReportCommand(
                resource.ownerId(),
                ownerType,
                periodStart,
                periodEnd,
                resource.categoryId(),
                format
        );
    }
}

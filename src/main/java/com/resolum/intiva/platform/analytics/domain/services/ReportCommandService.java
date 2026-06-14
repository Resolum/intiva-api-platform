package com.resolum.intiva.platform.analytics.domain.services;

import com.resolum.intiva.platform.analytics.domain.model.aggregates.AnalyticsReport;
import com.resolum.intiva.platform.analytics.domain.model.commands.GenerateReportCommand;

/**
 * Service interface for report generation commands.
 *
 * <p>Implementations fetch transactions through the ACL layer, render them into
 * the requested format (CSV or PDF), and return an {@link AnalyticsReport} with
 * the binary content ready for download.</p>
 */
public interface ReportCommandService {

    /**
     * Generates a downloadable report file based on the provided command parameters.
     *
     * <p>The generated file is not persisted; it is returned as an
     * {@link AnalyticsReport} aggregate that can be streamed directly to the HTTP
     * response.</p>
     *
     * @param command the generation parameters (owner, period, optional category, format)
     * @return an {@link AnalyticsReport} containing the binary content and file metadata
     * @throws com.resolum.intiva.platform.analytics.domain.model.exceptions.ReportGenerationException
     *         if an I/O error occurs during file generation
     */
    AnalyticsReport generateReport(GenerateReportCommand command);
}

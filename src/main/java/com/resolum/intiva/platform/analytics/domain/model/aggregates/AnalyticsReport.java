package com.resolum.intiva.platform.analytics.domain.model.aggregates;

import com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportFilter;
import com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportFormat;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;

import java.time.Instant;

/**
 * Aggregate root that represents a generated report in the analytics domain.
 *
 * <p>An {@code AnalyticsReport} is produced by the report generation command handler
 * and contains the binary content of the generated file (CSV or PDF) together with
 * metadata such as the filter used and the suggested file name for download.</p>
 *
 * <p>This aggregate is ephemeral — it is created on demand and served directly as a
 * downloadable response. It is not persisted to the database.</p>
 */
public class AnalyticsReport extends AuditableAbstractAggregate<AnalyticsReport> {

    /**
     * The filter parameters that were used to generate this report.
     */
    private final ReportFilter filter;

    /**
     * The suggested file name for the HTTP Content-Disposition header.
     */
    private final String fileName;

    /**
     * The binary content of the generated file.
     */
    private final byte[] content;

    /**
     * Creates an analytics report with the given filter, file name and binary content.
     * <p>The instance identifier and creation timestamp are inherited from
     * {@link AuditableAbstractAggregate}.</p>
     *
     * @param filter   the filter parameters used for generation
     * @param fileName the suggested download file name
     * @param content  the binary content of the generated file
     */
    public AnalyticsReport(ReportFilter filter, String fileName, byte[] content) {
        this.filter = filter;
        this.fileName = fileName;
        this.content = content;
    }

    /**
     * Returns the filter parameters used to generate this report.
     *
     * @return the report filter
     */
    public ReportFilter getFilter() {
        return filter;
    }

    /**
     * Returns the suggested file name for the HTTP Content-Disposition header.
     *
     * @return the file name (e.g. {@code report_individual_1_2026-01-01_2026-06-30.csv})
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the binary content of the generated report file.
     *
     * @return the file content as a byte array
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Returns the generation timestamp.
     *
     * @return the generation timestamp, or the current instant if not persisted
     */
    public Instant getGeneratedAt() {
        var createdAt = getCreatedAt();
        return createdAt != null ? createdAt : Instant.now();
    }

    /**
     * Returns the MIME content type based on the report format.
     *
     * @return {@code text/csv} for CSV format, {@code application/pdf} for PDF format
     */
    public String getContentType() {
        return filter.format() == ReportFormat.CSV ? "text/csv" : "application/pdf";
    }
}

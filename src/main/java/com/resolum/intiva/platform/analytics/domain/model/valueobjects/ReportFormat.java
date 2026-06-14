package com.resolum.intiva.platform.analytics.domain.model.valueobjects;

/**
 * Supported output formats for generated reports.
 *
 * <p>{@link #CSV} produces a comma-separated values file with transaction details
 * and a summary footer. {@link #PDF} produces a formatted PDF document with
 * summary tables, category breakdown, and transaction detail sections.</p>
 */
public enum ReportFormat {
    CSV, PDF
}

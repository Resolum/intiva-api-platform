package com.resolum.intiva.platform.analytics.domain.model.exceptions;

/**
 * Exception thrown when the requested report format is not a valid
 * {@link com.resolum.intiva.platform.analytics.domain.model.valueobjects.ReportFormat
 * ReportFormat} value.
 *
 * <p>This exception is mapped by the
 * {@link com.resolum.intiva.platform.shared.interfaces.rest.GlobalExceptionHandler
 * GlobalExceptionHandler} to an HTTP 400 (Bad Request) response.</p>
 */
public class InvalidReportFormatException extends RuntimeException {
    public InvalidReportFormatException(String message) {
        super(message);
    }
}

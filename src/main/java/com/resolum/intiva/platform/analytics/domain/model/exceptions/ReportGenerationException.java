package com.resolum.intiva.platform.analytics.domain.model.exceptions;

/**
 * Exception thrown when the report generation process fails due to an I/O error
 * or an internal error while generating the file content (CSV or PDF).
 *
 * <p>This exception wraps the underlying cause and is mapped by the
 * {@link com.resolum.intiva.platform.shared.interfaces.rest.GlobalExceptionHandler
 * GlobalExceptionHandler} to an HTTP 500 (Internal Server Error) response.</p>
 */
public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

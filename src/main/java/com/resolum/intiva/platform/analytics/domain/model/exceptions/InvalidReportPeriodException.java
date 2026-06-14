package com.resolum.intiva.platform.analytics.domain.model.exceptions;

/**
 * Exception thrown when the report period is invalid, specifically when the
 * {@code periodStart} date is after the {@code periodEnd} date.
 *
 * <p>This exception is mapped by the
 * {@link com.resolum.intiva.platform.shared.interfaces.rest.GlobalExceptionHandler
 * GlobalExceptionHandler} to an HTTP 400 (Bad Request) response.</p>
 */
public class InvalidReportPeriodException extends RuntimeException {
    public InvalidReportPeriodException(String message) {
        super(message);
    }
}

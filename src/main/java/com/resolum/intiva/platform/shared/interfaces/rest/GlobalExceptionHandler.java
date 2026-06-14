package com.resolum.intiva.platform.shared.interfaces.rest;

import com.resolum.intiva.platform.analytics.domain.model.exceptions.InvalidReportFormatException;
import com.resolum.intiva.platform.analytics.domain.model.exceptions.InvalidReportPeriodException;
import com.resolum.intiva.platform.analytics.domain.model.exceptions.ReportGenerationException;
import com.resolum.intiva.platform.household.domain.exceptions.InvitationAlreadyPendingException;
import com.resolum.intiva.platform.household.domain.exceptions.ResourceNotFoundException;
import com.resolum.intiva.platform.household.domain.exceptions.UnauthorizedException;
import com.resolum.intiva.platform.household.domain.exceptions.UserAlreadyMemberException;
import com.resolum.intiva.platform.categories.domain.model.exceptions.InactiveFinancialAccountException;
import com.resolum.intiva.platform.categories.domain.model.exceptions.InsufficientFundsException;
import com.resolum.intiva.platform.shared.domain.exceptions.ImageSizeExceededException;
import com.resolum.intiva.platform.shared.domain.exceptions.InvalidImageFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers.
 *
 * @summary
 * This class handles exceptions thrown by REST endpoints, providing appropriate error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException.
     * @param ex the exception
     * @return an ErrorResponse with BAD_REQUEST status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleException(IllegalArgumentException ex) {
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), ex.getMessage());
    }

    /**
     * Handles MethodArgumentNotValidException.
     * @param ex the exception
     * @return an ErrorResponse with BAD_REQUEST status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleException(MethodArgumentNotValidException ex) {
        String message = ex.getFieldErrors().stream().map(fieldError -> fieldError.getDefaultMessage() == null ? "" : fieldError.getDefaultMessage()).reduce("", String::concat);
        return ErrorResponse.create(ex, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), message);
    }

    /**
     * Handles HttpMessageNotReadableException.
     * @param ex the exception
     * @return a ProblemDetail with BAD_REQUEST status and the exception message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        ex.getMostSpecificCause().getMessage()
                );

        problemDetail.setTitle("Malformed JSON Request");

        return problemDetail;
    }

    /**
     * Handles InsufficientFundsException.
     * @param ex the exception
     * @return a ProblemDetail with BAD_REQUEST status and the exception message
     */
    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInsufficientFunds(
            InsufficientFundsException ex) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage()
                );

        problem.setTitle("Insufficient Funds");

        return problem;
    }

    /**
     * Handles InactiveFinancialAccountException.
     * @param ex the exception
     * @return a ProblemDetail with BAD_REQUEST status and the exception message
     */
    @ExceptionHandler(InactiveFinancialAccountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInactiveAccount(
            InactiveFinancialAccountException ex) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage()
                );

        problem.setTitle("Inactive Account");

        return problem;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleUnauthorized(UnauthorizedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidImageFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInvalidImageFormat(InvalidImageFormatException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ImageSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleImageSizeExceeded(ImageSizeExceededException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyMemberException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleUserAlreadyMember(UserAlreadyMemberException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvitationAlreadyPendingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleInvitationAlreadyPending(InvitationAlreadyPendingException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles {@link ReportGenerationException} thrown when report file generation fails.
     *
     * @param ex the exception
     * @return a ProblemDetail with INTERNAL_SERVER_ERROR status and a generic error message
     */
    @ExceptionHandler(ReportGenerationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleReportGeneration(ReportGenerationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating report");
    }

    /**
     * Handles {@link InvalidReportPeriodException} thrown when the report period is invalid
     * (period start after period end).
     *
     * @param ex the exception containing the specific validation message
     * @return a ProblemDetail with BAD_REQUEST status
     */
    @ExceptionHandler(InvalidReportPeriodException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInvalidReportPeriod(InvalidReportPeriodException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles {@link InvalidReportFormatException} thrown when an unsupported report format
     * is requested (not CSV or PDF).
     *
     * @param ex the exception
     * @return a ProblemDetail with BAD_REQUEST status and a generic format error message
     */
    @ExceptionHandler(InvalidReportFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInvalidReportFormat(InvalidReportFormatException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Format must be CSV or PDF");
    }
}

package com.resolum.intiva.platform.shared.interfaces.rest;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.exceptions.InactiveFinancialAccountException;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.exceptions.InsufficientFundsException;
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
}

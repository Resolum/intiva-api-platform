package com.resolum.intiva.platform.shared.domain.exceptions;

/**
 * Exception thrown when an image file has an unsupported format.
 * Only JPG, PNG and WEBP formats are accepted.
 */
public class InvalidImageFormatException extends RuntimeException {
    public InvalidImageFormatException(String format) {
        super("Image format '" + format + "' is not supported. Allowed formats: JPG, PNG, WEBP.");
    }
}

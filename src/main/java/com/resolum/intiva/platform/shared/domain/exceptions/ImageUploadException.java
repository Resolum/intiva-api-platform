package com.resolum.intiva.platform.shared.domain.exceptions;

/**
 * Custom exception to indicate errors during image upload operations.
 * This exception can be thrown when an image fails to upload to the storage service,
 * such as due to network issues, invalid file formats, or storage service errors.
 */
public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message) {
        super(message);
    }
}
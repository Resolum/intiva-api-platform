package com.resolum.intiva.platform.shared.domain.exceptions;

/**
 * Exception thrown when an image file exceeds the maximum allowed size.
 * The maximum allowed size is 5 MB.
 */
public class ImageSizeExceededException extends RuntimeException {
    public ImageSizeExceededException(long sizeInBytes) {
        super("Image size " + (sizeInBytes / (1024 * 1024.0)) + " MB exceeds the maximum allowed size of 5 MB.");
    }
}

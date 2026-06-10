package com.resolum.intiva.platform.shared.domain.valueobjects;

/**
 * Value object representing an image URL with its Cloudinary public ID.
 *
 * @param url      the URL string of the image
 * @param publicId the Cloudinary public ID used to manage/delete the asset
 */
public record ImageURL(
        String url,
        String publicId
) {

    // Regular expression to validate URL format
    private static final String URL_REGEX = "^(https?|ftp)://[^\\s]+$";

    // Validation logic in the constructor
    public ImageURL {
        if (url == null || url.isBlank() || !url.matches(URL_REGEX)) {
            throw new IllegalArgumentException("Image URL cannot be null or blank");
        }
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("Cloudinary public ID cannot be null or blank");
        }
    }

    /**
     * Returns the URL string.
     *
     * @return the URL string
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the Cloudinary public ID.
     *
     * @return the public ID string
     */
    public String getPublicId() {
        return publicId;
    }
}
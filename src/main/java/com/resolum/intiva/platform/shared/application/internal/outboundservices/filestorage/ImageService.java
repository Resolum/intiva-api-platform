package com.resolum.intiva.platform.shared.application.internal.outboundservices.filestorage;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Service interface for handling image uploads and deletions.
 */
public interface ImageService {

    /**
     * Uploads an image file and returns a map containing the public ID and URL of the uploaded image.
     *
     * @param fileData The byte array of the file to be uploaded.
     * @param fileName The name of the file to be uploaded.
     * @return A map containing the public ID and URL of the uploaded image.
     */
    Map<String, String> upload(byte[] fileData, String fileName);

    /**
     * Deletes an image based on its public ID.
     *
     * @param publicId The public ID of the image to be deleted.
     */
    void delete(String publicId);
}

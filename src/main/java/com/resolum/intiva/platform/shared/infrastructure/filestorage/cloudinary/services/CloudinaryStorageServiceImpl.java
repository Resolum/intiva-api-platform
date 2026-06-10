package com.resolum.intiva.platform.shared.infrastructure.filestorage.cloudinary.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.resolum.intiva.platform.shared.domain.exceptions.ImageSizeExceededException;
import com.resolum.intiva.platform.shared.domain.exceptions.ImageUploadException;
import com.resolum.intiva.platform.shared.domain.exceptions.InvalidImageFormatException;
import com.resolum.intiva.platform.shared.infrastructure.filestorage.cloudinary.CloudinaryStorageService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the CloudinaryStorageService interface.
 * This class provides methods to upload and delete images using Cloudinary.
 * Before uploading, it validates that the file format is JPG, PNG or WEBP
 * and that the file size does not exceed 5 MB.
 */
@Service
public class CloudinaryStorageServiceImpl implements CloudinaryStorageService {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    /**
     * Cloudinary instance used to interact with the Cloudinary API.
     * This instance is typically configured with API credentials and other settings.
     */
    private final Cloudinary cloudinary;

    /**
     * Constructor for CloudinaryStorageServiceImpl.
     *
     * @param cloudinary The Cloudinary instance to be used for API interactions.
     */
    public CloudinaryStorageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads a file to Cloudinary and returns a map containing the URL and public ID of the uploaded file.
     * Validates format (JPG, PNG, WEBP) and size (max 5 MB) before calling Cloudinary.
     *
     * @param fileData The byte array representing the file data to be uploaded.
     * @param fileName The original name of the file being uploaded.
     * @return A map containing the URL and public ID of the uploaded file.
     * @throws InvalidImageFormatException if the file extension is not JPG, PNG or WEBP.
     * @throws ImageSizeExceededException  if the file size exceeds 5 MB.
     * @throws ImageUploadException        if the Cloudinary API call fails.
     */
    @Override
    public Map<String, String> upload(byte[] fileData, String fileName) {
        validateFormat(fileName);
        validateSize(fileData);

        try {
            String uniqueFileName = generateUniqueFileName(fileName);

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", uniqueFileName,
                    "resource_type", "image",
                    "folder", "profiles"
            );

            Map result = cloudinary.uploader().upload(fileData, uploadParams);

            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            response.put("publicId", publicId);

            return response;

        } catch (IOException e) {
            throw new ImageUploadException("Cloudinary service is unavailable. Could not upload image: " + e.getMessage());
        }
    }

    /**
     * Deletes a file from Cloudinary using its public ID.
     *
     * @param publicId The public ID of the file to be deleted.
     * @throws ImageUploadException if the Cloudinary API call fails.
     */
    @Override
    public void delete(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new ImageUploadException("Cloudinary service is unavailable. Could not delete image: " + e.getMessage());
        }
    }

    /**
     * Validates that the file extension is one of the allowed formats: JPG, PNG or WEBP.
     *
     * @param fileName The name of the file to validate.
     * @throws InvalidImageFormatException if the extension is not allowed.
     */
    private void validateFormat(String fileName) {
        String extension = extractExtension(fileName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidImageFormatException(extension.isEmpty() ? "unknown" : extension);
        }
    }

    /**
     * Validates that the file size does not exceed the maximum allowed size of 5 MB.
     *
     * @param fileData The byte array of the file to validate.
     * @throws ImageSizeExceededException if the size exceeds 5 MB.
     */
    private void validateSize(byte[] fileData) {
        if (fileData.length > MAX_FILE_SIZE_BYTES) {
            throw new ImageSizeExceededException(fileData.length);
        }
    }

    /**
     * Generates a unique file name by appending a timestamp to the original file name (without extension).
     *
     * @param originalFileName The original name of the file.
     * @return A unique file name based on the original name and current timestamp.
     */
    private String generateUniqueFileName(String originalFileName) {
        String nameWithoutExtension = removeExtension(originalFileName);
        long timestamp = System.currentTimeMillis();
        return nameWithoutExtension + "_" + timestamp;
    }

    /**
     * Extracts the lowercase extension from a file name (without the dot).
     *
     * @param fileName The file name from which to extract the extension.
     * @return The lowercase extension, or an empty string if none is found.
     */
    private String extractExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * Removes the file extension from a given file name.
     *
     * @param fileName The original file name from which to remove the extension.
     * @return The file name without its extension. If the input is null or empty, returns "file".
     */
    private String removeExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "file";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }
}
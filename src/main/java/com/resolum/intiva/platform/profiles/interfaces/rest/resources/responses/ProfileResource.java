package com.resolum.intiva.platform.profiles.interfaces.rest.resources.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload that exposes a user profile through the REST API.
 *
 * @param id The unique identifier of the profile.
 * @param userId The unique identifier of the IAM user associated with this profile.
 * @param name The display name of the profile owner.
 * @param age The age of the profile owner.
 * @param avatarUrl The URL pointing to the user's avatar image.
 * @param phoneNumber The contact phone number of the profile owner.
 * @param bio A short biography or description of the profile owner.
 * @param email The email address of the IAM user associated with the profile (retrieved via ACL).
 */
@Schema(description = "User profile resource returned by the profiles API.")
public record ProfileResource(
        Long id,
        Long userId,
        String name,
        int age,
        String avatarUrl,
        String phoneNumber,
        String bio,
        String email
) {
}

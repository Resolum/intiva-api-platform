package com.resolum.intiva.platform.iam.domain.model.commands;

import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;
import org.springframework.web.multipart.MultipartFile;

/**
 * Command for signing up a new user.
 *
 * @param email     the email of the user to sign up
 * @param password  the password of the user to sign up
 * @param name      display name of the user
 * @param age       age of the user
 * @param phoneNumber contact phone number
 * @param bio       short biography
 * @param avatarFile avatar image file (optional)
 */
public record SignUpCommand(
        Email email,
        String password,
        String name,
        Integer age,
        String phoneNumber,
        String bio,
        MultipartFile avatarFile
) {
}

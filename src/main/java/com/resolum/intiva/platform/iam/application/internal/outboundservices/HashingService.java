package com.resolum.intiva.platform.iam.application.internal.outboundservices;

/**
 * HashingService is an interface that defines methods for encoding passwords and matching raw passwords with encoded passwords.
 * This service is typically used for securely storing user passwords and verifying them during authentication.
 */
public interface HashingService {

    /**
     * Encode a password
     * @param rawPassword the password to encode
     * @return String the encoded password
     */
    String encode(CharSequence rawPassword);

    /**
     * Match a raw password with an encoded password
     * @param rawPassword the raw password
     * @param encodedPassword the encoded password
     * @return boolean true if the raw password matches the encoded password, false otherwise
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}

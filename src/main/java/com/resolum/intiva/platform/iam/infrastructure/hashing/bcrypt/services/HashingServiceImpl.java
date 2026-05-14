package com.resolum.intiva.platform.iam.infrastructure.hashing.bcrypt.services;

import com.resolum.intiva.platform.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the BCryptHashingService interface using Spring Security's BCryptPasswordEncoder.
 */
@Service
public class HashingServiceImpl implements BCryptHashingService {

    // BCryptPasswordEncoder is a thread-safe class, so we can safely use a single instance for the entire application
    private final BCryptPasswordEncoder passwordEncoder;

    // Constructor initializes the BCryptPasswordEncoder
    HashingServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Hash a password using the BCrypt algorithm
     * @param rawPassword the password to hash
     * @return String the hashed password
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Check if a raw password matches a hashed password
     * @param rawPassword the raw password
     * @param encodedPassword the hashed password
     * @return boolean true if the raw password matches the hashed password, false otherwise
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

package com.resolum.intiva.platform.iam.infrastructure.hashing.bcrypt;

import com.resolum.intiva.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCryptHashingService is an interface that extends both HashingService and PasswordEncoder.
 * It provides methods for hashing and verifying passwords using the Bcrypt algorithm.
 */
public interface BCryptHashingService extends HashingService, PasswordEncoder {
}

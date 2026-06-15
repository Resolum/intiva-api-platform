package com.resolum.intiva.platform.iam.infrastructure.tokens.jwt;

import com.resolum.intiva.platform.iam.application.internal.outboundservices.token.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

/**
 * BearerTokenService is an interface that extends the TokenService interface and provides methods for handling JWT tokens in the context of bearer token authentication.
 * It defines methods for extracting the JWT token from an HTTP request and generating a JWT token from an authentication object.
 */
public interface BearerTokenService extends TokenService {

    /**
     * This method is responsible for extracting the JWT token from the HTTP request.
     * @param token the HTTP request
     * @return String the JWT token
     */
    String getBearerTokenFrom(HttpServletRequest token);

    /**
     * This method is responsible for generating a JWT token from an authentication object.
     * @param authentication the authentication object
     * @return String the JWT token
     * @see Authentication
     */
    String generateToken(Authentication authentication);
}

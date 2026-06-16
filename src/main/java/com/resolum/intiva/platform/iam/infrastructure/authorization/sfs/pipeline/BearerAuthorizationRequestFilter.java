package com.resolum.intiva.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.resolum.intiva.platform.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.resolum.intiva.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * BearerAuthorizationRequestFilter is a filter that intercepts HTTP requests to extract and validate bearer tokens for authentication.
 * It uses the BearerTokenService to handle token operations and UserDetailsService to load user details based on the token information.
 */
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    // Logger for logging information and errors
    private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthorizationRequestFilter.class);

    // Service for handling bearer token operations
    private final BearerTokenService tokenService;

    // Service for loading user details, qualified to ensure the correct bean is injected
    @Qualifier("defaultUserDetailsService")
    private final UserDetailsService userDetailsService;

    /**
     * Constructor for BearerAuthorizationRequestFilter.
     * @param tokenService the bearer token service
     * @param userDetailsService the user details service
     */
    public BearerAuthorizationRequestFilter(BearerTokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * This method is responsible for filtering requests and setting the user authentication.
     * @param request The request object.
     * @param response The response object.
     * @param filterChain The filter chain object.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = tokenService.getBearerTokenFrom(request);
            LOGGER.info("Method: {}, ContentType: {}, Token: {}", request.getMethod(), request.getContentType(), token);
            if (token == null) {
                LOGGER.info("Authorization header is null. All headers:");
                request.getHeaderNames().asIterator().forEachRemaining(header ->
                    LOGGER.info("  {}: {}", header, request.getHeader(header))
                );
            }
            if (token != null && tokenService.validateToken(token)) {
                String username = tokenService.getUsernameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request));
            } else {
                LOGGER.info("Token is not valid");
            }

        } catch (Exception e) {
            LOGGER.error("Cannot set user authentication: {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }
}

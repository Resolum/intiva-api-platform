package com.resolum.intiva.platform.iam.infrastructure.authorization.sfs.model;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/**
 * Builder for creating a UsernamePasswordAuthenticationToken with the provided UserDetails and HttpServletRequest.
 * This is used to authenticate a user based on their username and password, and to set the appropriate details for the authentication process.
 */
public class UsernamePasswordAuthenticationTokenBuilder {

    /**
     * Builds a UsernamePasswordAuthenticationToken using the provided UserDetails and HttpServletRequest.
     * @param principal the UserDetails object representing the authenticated user
     * @param request the HttpServletRequest containing the details of the authentication request
     * @return a UsernamePasswordAuthenticationToken with the user's details and authorities, and the request details set
     */
    public static UsernamePasswordAuthenticationToken build(UserDetails principal, HttpServletRequest request) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }
}

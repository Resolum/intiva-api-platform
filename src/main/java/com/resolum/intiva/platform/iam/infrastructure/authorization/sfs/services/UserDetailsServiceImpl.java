package com.resolum.intiva.platform.iam.infrastructure.authorization.sfs.services;

import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;
import com.resolum.intiva.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for loading user details from the database and providing them to the Spring Security framework.
 * It implements the UserDetailsService interface, which is a core interface in Spring Security for retrieving user-related data.
 */
@Service(value = "defaultUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repository for accessing user data from the database
    private final UserRepository userRepository;

    /**
     * Constructor for UserDetailsServiceImpl.
     * @param userRepository the user repository
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method is responsible for loading the user details from the database.
     * @param username The username.
     * @return The UserDetails object.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        var user = userRepository.findUserByEmail(new Email(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return UserDetailsImpl.build(user);
    }
}

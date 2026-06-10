package com.resolum.intiva.platform.profiles.application.internal.eventhandlers;

import com.resolum.intiva.platform.iam.domain.model.events.UserRegisteredEvent;
import com.resolum.intiva.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.resolum.intiva.platform.profiles.domain.model.services.ProfileCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Listens for UserRegisteredEvent published by the IAM context and automatically
 * creates a profile for the new user within the profiles bounded context.
 */
@Service("profilesUserRegisteredEventHandler")
public class UserRegisteredEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRegisteredEventHandler.class);

    private final ProfileCommandService profileCommandService;

    public UserRegisteredEventHandler(ProfileCommandService profileCommandService) {
        this.profileCommandService = profileCommandService;
    }

    /**
     * Handles the UserRegisteredEvent by creating a default profile.
     * The display name is derived from the email local-part (everything before '@').
     *
     * @param event the domain event published when a user registers
     */
    @EventListener
    public void on(UserRegisteredEvent event) {
        Long userId = event.getUserId();
        String email = event.getUser().getEmail().getValue();
        String defaultName = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;

        LOGGER.info("Creating profile for new user [id={}]", userId);

        var command = new CreateProfileCommand(userId, defaultName);
        var profile = profileCommandService.handle(command);

        LOGGER.info("Profile created [profileId={}, userId={}]", profile.getId(), userId);
    }
}

package com.resolum.intiva.platform.iam.application.internal.eventhandlers;

import com.resolum.intiva.platform.iam.application.internal.outboundservices.acl.IamExternalCategoriesService;
import com.resolum.intiva.platform.iam.application.internal.outboundservices.acl.IamExternalFinancialAccountsService;
import com.resolum.intiva.platform.iam.domain.model.commands.CreateUserOnboardingCommand;
import com.resolum.intiva.platform.iam.domain.model.events.UserRegisteredEvent;
import com.resolum.intiva.platform.iam.domain.services.OnboardingCommandService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 *  * Event handler for UserRegisteredEvent. This class listens for UserRegisteredEvent and creates a default category for the user using the ExternalCategoriesService.
 */
@Service
public class UserRegisteredEventHandler {

    /**
     * The ExternalCategoriesService is a service that interacts with external categories services.
     */
    private final IamExternalCategoriesService iamExternalCategoriesService;

    /**
     * The ExternalFinancialAccountsService is a service that interacts with external financial accounts services.
     */
    private final IamExternalFinancialAccountsService iamExternalFinancialAccountsService;

    private final OnboardingCommandService onboardingCommandService;

    /**
     * Constructor for UserRegisteredEventHandler.
     * @param iamExternalCategoriesService the ExternalCategoriesService to be used by this event handler
     */
    public UserRegisteredEventHandler(IamExternalCategoriesService iamExternalCategoriesService, IamExternalFinancialAccountsService iamExternalFinancialAccountsService, OnboardingCommandService onboardingCommandService) {
        this.iamExternalCategoriesService = iamExternalCategoriesService;
        this.iamExternalFinancialAccountsService = iamExternalFinancialAccountsService;
        this.onboardingCommandService = onboardingCommandService;
    }

    /**
     * Handles the UserRegisteredEvent by creating a default category for the user.
     * @param event The UserRegisteredEvent object containing the user ID.
     */
    @EventListener
    public void on(UserRegisteredEvent event) {
        iamExternalCategoriesService.createDefaultCategory(event.getUserId());
        iamExternalFinancialAccountsService.createDefaultFinancialAccount(event.getUserId());
        onboardingCommandService.handle(new CreateUserOnboardingCommand(
                event.getUserId()
        ));
    }
}

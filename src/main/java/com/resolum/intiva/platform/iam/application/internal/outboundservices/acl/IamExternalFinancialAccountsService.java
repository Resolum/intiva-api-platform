package com.resolum.intiva.platform.iam.application.internal.outboundservices.acl;

import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl.FinancialAccountContextFacade;
import org.springframework.stereotype.Service;

/**
 * Service class for interacting with external financial accounts services.
 */
@Service
public class IamExternalFinancialAccountsService {

    /**
     * The FinancialAccountContextFacade is a facade that provides access to the financial account context.
     */
    private final FinancialAccountContextFacade financialAccountContextFacade;

    /**
     * Constructor for ExternalFinancialAccountsService.
     * @param financialAccountContextFacade the FinancialAccountContextFacade to be used by this service
     */
    public IamExternalFinancialAccountsService(FinancialAccountContextFacade financialAccountContextFacade) {
        this.financialAccountContextFacade = financialAccountContextFacade;
    }

    /**
     * Creates a default financial account for a user.
     * @param ownerId the owner id of the financial account
     */
    public void createDefaultFinancialAccount(Long ownerId) {
        financialAccountContextFacade.createDefaultFinancialAccount(ownerId);
    }
}

package com.resolum.intiva.platform.finances.application.internal.outboundservices.acl;

import com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl.FinancialAccountContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for interacting with external financial account services.
 */
@Service
public class FinancesExternalFinancialAccountService {

    /**
     * The FinancialAccountContextFacade is a facade that provides access to the financial account context.
     */
    private final FinancialAccountContextFacade financialAccountContextFacade;

    /**
     * Constructor for FinancesExternalFinancialAccountService.
     *
     * @param financialAccountContextFacade the FinancialAccountContextFacade to be used by this service
     */
    public FinancesExternalFinancialAccountService(FinancialAccountContextFacade financialAccountContextFacade) {
        this.financialAccountContextFacade = financialAccountContextFacade;
    }

    /**
     * Checks if a financial account exists by its ID.
     * @param financialAccountId the ID of the financial account to check
     * @return an Optional containing a boolean indicating whether the financial account exists
     */
    public Optional<Boolean> existsFinancialAccountById(Long financialAccountId) {
        return Optional.of(financialAccountContextFacade.existsFinancialAccountById(financialAccountId));
    }
}

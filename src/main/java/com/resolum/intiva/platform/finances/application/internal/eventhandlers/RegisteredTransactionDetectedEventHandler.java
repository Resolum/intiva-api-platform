package com.resolum.intiva.platform.finances.application.internal.eventhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.domain.model.events.RegisteredTransactionDetectedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Event handler for RegisteredTransactionDetectedEvent. This class listens for RegisteredTransactionDetectedEvent and creates a financial account transaction using the FinancesExternalFinancialAccountService.
 */
@Service
public class RegisteredTransactionDetectedEventHandler {

    /**
     * The FinancesExternalFinancialAccountService is a service that interacts with external financial account services.
     */
    private final FinancesExternalFinancialAccountService financesExternalFinancialAccountService;

    /**
     * Constructor for RegisteredTransactionDetectedEventHandler.
     * @param financesExternalFinancialAccountService the FinancesExternalFinancialAccountService to be used by this event handler
     */
    public RegisteredTransactionDetectedEventHandler(FinancesExternalFinancialAccountService financesExternalFinancialAccountService) {
        this.financesExternalFinancialAccountService = financesExternalFinancialAccountService;
    }

    /**
     * Handles the RegisteredTransactionDetectedEvent by creating a financial account transaction.
     * @param event The RegisteredTransactionDetectedEvent object containing the transaction details.
     */
    @EventListener
    public void on(RegisteredTransactionDetectedEvent event) {
        financesExternalFinancialAccountService.createFinancialAccountTransaction(
                event.getFinancialAccountId(),
                event.getTransactionType(),
                event.getAmount(),
                event.getCurrencyCode()
        );
    }
}

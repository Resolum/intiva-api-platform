package com.resolum.intiva.platform.paymentmethodsandcategories.domain.services;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateFinancialAccountCommand;

/**
 * Service interface for handling financial account-related commands.
 */
public interface FinancialAccountCommandService {

    /**
     * Handles the creation of a new financial account.
     * @param command the command containing the financial account details
     */
    void handle(CreateDefaultFinancialAccountCommand command);
}

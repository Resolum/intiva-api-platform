package com.resolum.intiva.platform.paymentmethodsandcategories.domain.services;

import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateFinancialAccountCommand;
import com.resolum.intiva.platform.paymentmethodsandcategories.domain.model.commands.CreateFinancialAccountTransaction;

/**
 * Service interface for handling financial account-related commands.
 */
public interface FinancialAccountCommandService {

    /**
     * Handles the creation of a new financial account.
     * @param command the command containing the financial account details
     */
    void handle(CreateDefaultFinancialAccountCommand command);

    /**
     * Handles the creation of a new financial account transaction.
     * @param command the command containing the financial account transaction details
     */
    void handle(CreateFinancialAccountTransaction command);
}

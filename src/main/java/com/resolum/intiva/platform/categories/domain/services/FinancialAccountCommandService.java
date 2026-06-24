package com.resolum.intiva.platform.categories.domain.services;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateFinancialAccountTransaction;
import com.resolum.intiva.platform.categories.domain.model.commands.UpdateFinancialAccountCommand;

public interface FinancialAccountCommandService {

    void handle(CreateDefaultFinancialAccountCommand command);

    void handle(CreateFinancialAccountTransaction command);

    FinancialAccount handle(CreateFinancialAccountCommand command);

    FinancialAccount handle(UpdateFinancialAccountCommand command);
}

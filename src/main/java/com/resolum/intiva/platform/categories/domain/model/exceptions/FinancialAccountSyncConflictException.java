package com.resolum.intiva.platform.categories.domain.model.exceptions;

import java.math.BigDecimal;

public class FinancialAccountSyncConflictException extends RuntimeException {

    public FinancialAccountSyncConflictException(Long financialAccountId, Long baseAccountVersion, Long currentAccountVersion, BigDecimal currentBalance, BigDecimal attemptedAmount) {
        super("Financial account " + financialAccountId + " changed since offline capture. Base version: "
                + baseAccountVersion + ", current version: " + currentAccountVersion
                + ", current balance: " + currentBalance + ", attempted amount: " + attemptedAmount + ".");
    }
}

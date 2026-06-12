package com.resolum.intiva.platform.categories.interfaces.rest.resources.responses;

import java.math.BigDecimal;

/**
 * Resource representing a financial account.
 * @param id the id of the financial account
 * @param name the name of the financial account
 * @param accountType the type of the financial account
 * @param currencyCode the currency code of the financial account
 * @param currentAmount the current amount in the financial account
 * @param institution the institution associated with the financial account
 * @param creditLimit the credit limit of the financial account
 * @param isActive the status of the financial account
 */
public record FinancialAccountResource(
        Long id,
        String name,
        String accountType,
        String currencyCode,
        BigDecimal currentAmount,
        String institution,
        BigDecimal creditLimit,
        Boolean isActive
) {}

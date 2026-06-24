package com.resolum.intiva.platform.categories.domain.model.commands;

import java.math.BigDecimal;

public record CreateFinancialAccountCommand(
        String name,
        String accountType,
        String currency,
        BigDecimal creditLimit,
        BigDecimal initialAmount,
        String institution,
        Long ownerId
) {}

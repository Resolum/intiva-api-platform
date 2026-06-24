package com.resolum.intiva.platform.categories.domain.model.commands;

public record UpdateFinancialAccountCommand(
        Long accountId,
        String name,
        Boolean isActive,
        Long expectedVersion
) {}
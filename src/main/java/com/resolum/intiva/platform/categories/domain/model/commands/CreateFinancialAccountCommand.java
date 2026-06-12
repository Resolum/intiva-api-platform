package com.resolum.intiva.platform.categories.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command object for creating a new financial account.
 *
 * @param name         The name of the financial account.
 * @param accountType  The type of the financial account (e.g., CASH, CREDIT_CARD).
 * @param currency     The currency code (e.g., USD, EUR).
 * @param creditLimit  The credit limit for the account (nullable for non-credit accounts).
 * @param initialAmount The initial amount in the account.
 * @param institution   The financial institution associated with the account.
 * @param ownerId      The ID of the user who owns the account.
 */
public record CreateFinancialAccountCommand(
        String name,
        String accountType,
        String currency,
        String symbol,
        BigDecimal creditLimit,
        BigDecimal initialAmount,
        String institution,
        Long ownerId
) {}

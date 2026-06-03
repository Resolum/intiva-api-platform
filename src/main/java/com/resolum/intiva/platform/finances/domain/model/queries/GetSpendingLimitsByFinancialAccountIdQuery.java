package com.resolum.intiva.platform.finances.domain.model.queries;

/**
 * Query used to retrieve spending limits that target a financial account.
 *
 * @param financialAccountId financial account identifier
 */
public record GetSpendingLimitsByFinancialAccountIdQuery(Long financialAccountId) {
}

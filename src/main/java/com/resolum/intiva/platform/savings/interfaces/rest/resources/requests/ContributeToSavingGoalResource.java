package com.resolum.intiva.platform.savings.interfaces.rest.resources.requests;

import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import java.math.BigDecimal;

/**
 * Resource representing a request to make a contribution to a saving goal.
 *
 * @param amount        the amount of money to contribute
 * @param currencyCode  the currency code of the contribution
 * @param contributorId the ID of the user making the contribution
 */
public record ContributeToSavingGoalResource(
    BigDecimal amount,
    CurrencyCodes currencyCode,
    Long contributorId
) {}

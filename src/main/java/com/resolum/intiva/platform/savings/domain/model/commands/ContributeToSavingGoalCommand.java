package com.resolum.intiva.platform.savings.domain.model.commands;

import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import java.math.BigDecimal;

/**
 * Command to contribute to an existing saving goal.
 *
 * @param savingGoalId  the ID of the saving goal to contribute to
 * @param amount        the amount of money to contribute
 * @param currencyCode  the currency code of the contribution
 * @param contributorId the ID of the user making the contribution
 */
public record ContributeToSavingGoalCommand(
    Long savingGoalId,
    BigDecimal amount,
    CurrencyCodes currencyCode,
    Long contributorId
) {}

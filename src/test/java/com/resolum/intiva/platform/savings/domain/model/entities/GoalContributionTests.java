package com.resolum.intiva.platform.savings.domain.model.entities;

import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GoalContribution entity.
 * Covers creation with valid parameters and verifies field initialization.
 */
public class GoalContributionTests {

    /**
     * Verifies that a GoalContribution is created successfully when all
     * parameters are valid, and that contributedAt is set automatically.
     */
    @Test
    void create_shouldCreateGoalContribution_whenParametersAreValid() {
        // Arrange
        var amount = new Money(BigDecimal.valueOf(150), CurrencyCodes.PEN);
        var contributorId = 1L;
        var savingGoalId = 10L;

        // Act
        var contribution = new GoalContribution(amount, contributorId, savingGoalId);

        // Assert
        assertNotNull(contribution);
        assertEquals(BigDecimal.valueOf(150), contribution.getAmountContributed().amount());
        assertEquals(contributorId, contribution.getContributorId());
        assertEquals(savingGoalId, contribution.getSavingGoalId());
        assertNotNull(contribution.getContributedAt());
    }

    /**
     * Verifies that a GoalContribution can be created with a USD currency code,
     * confirming that the currency is stored correctly.
     */
    @Test
    void create_shouldCreateGoalContribution_whenCurrencyIsUSD() {
        // Arrange
        var amount = new Money(BigDecimal.valueOf(50), CurrencyCodes.USD);
        var contributorId = 2L;
        var savingGoalId = 5L;

        // Act
        var contribution = new GoalContribution(amount, contributorId, savingGoalId);

        // Assert
        assertNotNull(contribution);
        assertEquals(CurrencyCodes.USD, contribution.getAmountContributed().currencyCode());
    }
}

package com.resolum.intiva.platform.finances.domain.model.aggregates;

import com.resolum.intiva.platform.finances.domain.model.commands.CreateSpendingLimitCommand;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitExceededEvent;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitWarningReachedEvent;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitStatus;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Domain tests for {@link SpendingLimit}.
 *
 * <p>These tests verify that the aggregate emits the notification-oriented domain events when
 * consumption crosses the configured warning and exceeded thresholds.</p>
 */
class SpendingLimitTests {

    /**
     * Verifies that consuming 80 percent or more of the limit raises the warning event exactly once.
     */
    @Test
    void registerExpense_shouldRaiseWarningEvent_whenConsumptionReachesWarningThreshold() {
        // Arrange
        var spendingLimit = buildSpendingLimit();

        // Act
        spendingLimit.registerExpense(new Money(BigDecimal.valueOf(400), CurrencyCodes.PEN));

        // Assert
        assertEquals(SpendingLimitStatus.WARNING, spendingLimit.getStatus());
        var events = getDomainEvents(spendingLimit);
        assertEquals(1, events.size());
        assertInstanceOf(SpendingLimitWarningReachedEvent.class, events.iterator().next());
    }

    /**
     * Verifies that consuming the full amount or more raises the exceeded event exactly once.
     */
    @Test
    void registerExpense_shouldRaiseExceededEvent_whenConsumptionReachesOrExceedsLimit() {
        // Arrange
        var spendingLimit = buildSpendingLimit();

        // Act
        spendingLimit.registerExpense(new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN));

        // Assert
        assertEquals(SpendingLimitStatus.EXCEEDED, spendingLimit.getStatus());
        var events = getDomainEvents(spendingLimit);
        assertEquals(1, events.size());
        assertInstanceOf(SpendingLimitExceededEvent.class, events.iterator().next());
    }

    /**
     * Builds a valid spending limit for threshold notification tests.
     *
     * @return spending-limit aggregate with a PEN 500 ceiling
     */
    private SpendingLimit buildSpendingLimit() {
        return new SpendingLimit(new CreateSpendingLimitCommand(
                7L,
                OwnerTypes.INDIVIDUAL,
                SpendingLimitTargetType.CATEGORY,
                12L,
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        ));
    }

    /**
     * Reads the uncommitted domain events registered by the aggregate root.
     *
     * @param spendingLimit aggregate root under test
     * @return registered domain events
     */
    @SuppressWarnings("unchecked")
    private Collection<Object> getDomainEvents(SpendingLimit spendingLimit) {
        return (Collection<Object>) ReflectionTestUtils.invokeMethod(spendingLimit, "domainEvents");
    }
}

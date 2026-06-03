package com.resolum.intiva.platform.finances.domain.model.aggregates;

import com.resolum.intiva.platform.finances.domain.model.commands.CreateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.RecurringFrequency;
import com.resolum.intiva.platform.shared.domain.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RecurringTransaction} aggregate.
 *
 * <p>These tests cover the aggregate's own business rules: creation, due-date evaluation,
 * schedule advancement and deactivation after the configured end date.</p>
 */
public class RecurringTransactionTests {

    /**
     * Verifies that a recurring transaction is created successfully with the first execution
     * scheduled on its start date.
     */
    @Test
    void create_shouldInitializeRecurringTransaction_whenCommandIsValid() {
        // Arrange
        var startDate = LocalDate.of(2026, 6, 2);

        // Act
        var recurringTransaction = buildRecurringTransaction(startDate, RecurringFrequency.MONTHLY, null);

        // Assert
        assertNotNull(recurringTransaction);
        assertEquals(startDate, recurringTransaction.getStartDate());
        assertEquals(startDate, recurringTransaction.getNextExecutionDate());
        assertNull(recurringTransaction.getLastExecutionDate());
        assertTrue(recurringTransaction.getActive());
    }

    /**
     * Verifies that daily recurring transactions advance exactly one day after a successful execution.
     */
    @Test
    void registerExecution_shouldAdvanceOneDay_whenFrequencyIsDaily() {
        // Arrange
        var startDate = LocalDate.of(2026, 6, 2);
        var recurringTransaction = buildRecurringTransaction(startDate, RecurringFrequency.DAILY, null);

        // Act
        recurringTransaction.registerExecution();

        // Assert
        assertEquals(startDate, recurringTransaction.getLastExecutionDate());
        assertEquals(startDate.plusDays(1), recurringTransaction.getNextExecutionDate());
        assertTrue(recurringTransaction.getActive());
    }

    /**
     * Verifies that annual recurring transactions advance exactly one year after a successful execution.
     */
    @Test
    void registerExecution_shouldAdvanceOneYear_whenFrequencyIsAnnual() {
        // Arrange
        var startDate = LocalDate.of(2026, 6, 2);
        var recurringTransaction = buildRecurringTransaction(startDate, RecurringFrequency.ANNUAL, null);

        // Act
        recurringTransaction.registerExecution();

        // Assert
        assertEquals(startDate, recurringTransaction.getLastExecutionDate());
        assertEquals(startDate.plusYears(1), recurringTransaction.getNextExecutionDate());
        assertTrue(recurringTransaction.getActive());
    }

    /**
     * Verifies that a recurring transaction stops being due after its next execution date moves beyond endDate.
     */
    @Test
    void registerExecution_shouldDeactivateRecurringTransaction_whenNextExecutionDatePassesEndDate() {
        // Arrange
        var startDate = LocalDate.of(2026, 6, 2);
        var recurringTransaction = buildRecurringTransaction(startDate, RecurringFrequency.MONTHLY, startDate);

        // Act
        recurringTransaction.registerExecution();

        // Assert
        assertFalse(recurringTransaction.getActive());
        assertEquals(startDate, recurringTransaction.getLastExecutionDate());
        assertEquals(startDate.plusMonths(1), recurringTransaction.getNextExecutionDate());
        assertFalse(recurringTransaction.isDue(startDate.plusMonths(1)));
    }

    /**
     * Verifies that the due-date rule only returns true on or after the configured next execution date.
     */
    @Test
    void isDue_shouldReturnTrueOnlyOnOrAfterNextExecutionDate() {
        // Arrange
        var startDate = LocalDate.of(2026, 6, 10);
        var recurringTransaction = buildRecurringTransaction(startDate, RecurringFrequency.WEEKLY, null);

        // Act & Assert
        assertFalse(recurringTransaction.isDue(startDate.minusDays(1)));
        assertTrue(recurringTransaction.isDue(startDate));
        assertTrue(recurringTransaction.isDue(startDate.plusDays(3)));
    }

    /**
     * Creates a valid recurring transaction aggregate for domain tests.
     *
     * @param startDate first execution date
     * @param frequency recurring cadence
     * @param endDate optional inclusive final execution date
     * @return recurring transaction aggregate
     */
    private RecurringTransaction buildRecurringTransaction(
            LocalDate startDate,
            RecurringFrequency frequency,
            LocalDate endDate
    ) {
        return new RecurringTransaction(new CreateRecurringTransactionCommand(
                new Money(BigDecimal.valueOf(1500), CurrencyCodes.PEN),
                "Sueldo",
                1L,
                new FinancialAccountId(3L),
                new UserId(1L),
                TransactionTypes.INCOME,
                new CategoryId(5L),
                OwnerTypes.INDIVIDUAL,
                frequency,
                startDate,
                endDate
        ));
    }
}

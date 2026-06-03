package com.resolum.intiva.platform.finances.application.internal.jobhandlers;

import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.finances.domain.model.commands.CreateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.events.RecurringTransactionExecutionRequestedEvent;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.RecurringFrequency;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RecurringTransactionJobHandler}.
 *
 * <p>These tests focus on the application orchestration performed by the batch handler:
 * locating due recurring definitions, publishing one execution request event per due schedule and
 * repeating the publication until the recurring definition is no longer due.</p>
 */
public class RecurringTransactionJobHandlerTests {

    /**
     * Verifies that a due recurring transaction publishes one execution request event.
     */
    @Test
    void executeDueRecurringTransactions_shouldPublishExecutionEvent_whenRecurringTransactionIsDue() {
        // Arrange
        var recurringTransactionRepository = mock(RecurringTransactionRepository.class);
        var eventPublisher = mock(ApplicationEventPublisher.class);
        var jobHandler = new RecurringTransactionJobHandler(recurringTransactionRepository, eventPublisher);
        var today = LocalDate.now();
        var recurringTransaction = buildRecurringTransaction(today, RecurringFrequency.MONTHLY, null);

        when(recurringTransactionRepository.findAllByActiveTrueAndNextExecutionDateLessThanEqual(today))
                .thenReturn(List.of(recurringTransaction));
        doAnswer(invocation -> {
            var event = invocation.getArgument(0, RecurringTransactionExecutionRequestedEvent.class);
            event.getRecurringTransaction().registerExecution();
            return null;
        }).when(eventPublisher).publishEvent(any(RecurringTransactionExecutionRequestedEvent.class));

        // Act
        jobHandler.executeDueRecurringTransactions();

        // Assert
        verify(eventPublisher, times(1)).publishEvent(any(RecurringTransactionExecutionRequestedEvent.class));
        assertEquals(today.plusMonths(1), recurringTransaction.getNextExecutionDate());
        assertEquals(today, recurringTransaction.getLastExecutionDate());
    }

    /**
     * Verifies that the handler catches up multiple missed executions for the same recurring definition
     * until the next execution date moves beyond the current day.
     */
    @Test
    void executeDueRecurringTransactions_shouldCatchUpMissedExecutions_whenRecurringTransactionHasPastDueDates() {
        // Arrange
        var recurringTransactionRepository = mock(RecurringTransactionRepository.class);
        var eventPublisher = mock(ApplicationEventPublisher.class);
        var jobHandler = new RecurringTransactionJobHandler(recurringTransactionRepository, eventPublisher);
        var today = LocalDate.now();
        var recurringTransaction = buildRecurringTransaction(today.minusWeeks(2), RecurringFrequency.WEEKLY, null);

        when(recurringTransactionRepository.findAllByActiveTrueAndNextExecutionDateLessThanEqual(today))
                .thenReturn(List.of(recurringTransaction));
        doAnswer(invocation -> {
            var event = invocation.getArgument(0, RecurringTransactionExecutionRequestedEvent.class);
            event.getRecurringTransaction().registerExecution();
            return null;
        }).when(eventPublisher).publishEvent(any(RecurringTransactionExecutionRequestedEvent.class));

        // Act
        jobHandler.executeDueRecurringTransactions();

        // Assert
        verify(eventPublisher, times(3)).publishEvent(any(RecurringTransactionExecutionRequestedEvent.class));
        assertEquals(today.plusWeeks(1), recurringTransaction.getNextExecutionDate());
        assertEquals(today, recurringTransaction.getLastExecutionDate());
        assertFalse(recurringTransaction.isDue(today));
    }

    /**
     * Creates a recurring transaction aggregate tailored for job execution tests.
     *
     * @param startDate first execution date
     * @param frequency recurring cadence
     * @param endDate optional inclusive final execution date
     * @return recurring transaction aggregate ready to be consumed by the handler
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

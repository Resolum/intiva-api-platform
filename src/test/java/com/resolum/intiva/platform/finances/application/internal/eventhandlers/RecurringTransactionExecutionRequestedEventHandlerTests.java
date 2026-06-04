package com.resolum.intiva.platform.finances.application.internal.eventhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.RecurringTransaction;
import com.resolum.intiva.platform.finances.domain.model.aggregates.Transaction;
import com.resolum.intiva.platform.finances.domain.model.commands.CreateRecurringTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.RegisterTransactionCommand;
import com.resolum.intiva.platform.finances.domain.model.events.RecurringTransactionExecutionRequestedEvent;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.RecurringFrequency;
import com.resolum.intiva.platform.finances.domain.services.TransactionCommandService;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.RecurringTransactionRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RecurringTransactionExecutionRequestedEventHandler}.
 *
 * <p>These tests verify the new balance-aware orchestration for recurring expenses before they are materialized
 * into normal transactions.</p>
 */
public class RecurringTransactionExecutionRequestedEventHandlerTests {

    /**
     * Verifies that an expense recurring definition is skipped when the referenced financial account
     * does not have enough balance to cover the requested amount.
     */
    @Test
    void on_shouldSkipExecution_whenExpenseRecurringTransactionDoesNotHaveEnoughBalance() {
        // Arrange
        var transactionCommandService = mock(TransactionCommandService.class);
        var financesExternalFinancialAccountService = mock(FinancesExternalFinancialAccountService.class);
        var recurringTransactionRepository = mock(RecurringTransactionRepository.class);
        var handler = new RecurringTransactionExecutionRequestedEventHandler(
                transactionCommandService,
                financesExternalFinancialAccountService,
                recurringTransactionRepository
        );
        var today = LocalDate.now();
        var recurringTransaction = buildRecurringExpense(today);
        var event = new RecurringTransactionExecutionRequestedEvent(this, recurringTransaction);

        when(financesExternalFinancialAccountService.hasSufficientBalance(3L, BigDecimal.valueOf(180)))
                .thenReturn(false);

        // Act
        handler.on(event);

        // Assert
        verify(transactionCommandService, never()).handle(any(RegisterTransactionCommand.class));
        verify(recurringTransactionRepository, never()).save(any(RecurringTransaction.class));
        assertEquals(today, recurringTransaction.getNextExecutionDate());
        assertNull(recurringTransaction.getLastExecutionDate());
    }

    /**
     * Verifies that a valid recurring execution registers a normal transaction and advances the schedule.
     */
    @Test
    void on_shouldRegisterTransactionAndAdvanceSchedule_whenBalanceIsSufficient() {
        // Arrange
        var transactionCommandService = mock(TransactionCommandService.class);
        var financesExternalFinancialAccountService = mock(FinancesExternalFinancialAccountService.class);
        var recurringTransactionRepository = mock(RecurringTransactionRepository.class);
        var handler = new RecurringTransactionExecutionRequestedEventHandler(
                transactionCommandService,
                financesExternalFinancialAccountService,
                recurringTransactionRepository
        );
        var today = LocalDate.now();
        var recurringTransaction = buildRecurringExpense(today);
        var event = new RecurringTransactionExecutionRequestedEvent(this, recurringTransaction);

        when(financesExternalFinancialAccountService.hasSufficientBalance(3L, BigDecimal.valueOf(180)))
                .thenReturn(true);
        when(transactionCommandService.handle(any(RegisterTransactionCommand.class)))
                .thenReturn(Optional.of(mock(Transaction.class)));
        when(recurringTransactionRepository.save(any(RecurringTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        handler.on(event);

        // Assert
        verify(transactionCommandService, times(1)).handle(any(RegisterTransactionCommand.class));
        verify(recurringTransactionRepository, times(1)).save(recurringTransaction);
        assertEquals(today.plusMonths(1), recurringTransaction.getNextExecutionDate());
        assertEquals(today, recurringTransaction.getLastExecutionDate());
    }

    /**
     * Creates an expense recurring transaction suitable for balance-validation tests.
     *
     * @param startDate first execution date
     * @return recurring expense aggregate
     */
    private RecurringTransaction buildRecurringExpense(LocalDate startDate) {
        return new RecurringTransaction(new CreateRecurringTransactionCommand(
                new Money(BigDecimal.valueOf(180), CurrencyCodes.PEN),
                "Pago recurrente de servicios",
                1L,
                new FinancialAccountId(3L),
                new UserId(1L),
                TransactionTypes.EXPENSE,
                new CategoryId(8L),
                OwnerTypes.INDIVIDUAL,
                RecurringFrequency.MONTHLY,
                startDate,
                null
        ));
    }
}

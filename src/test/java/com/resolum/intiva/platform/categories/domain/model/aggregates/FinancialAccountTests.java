package com.resolum.intiva.platform.categories.domain.model.aggregates;

import com.resolum.intiva.platform.categories.domain.model.entities.DebitCardAccount;
import com.resolum.intiva.platform.categories.domain.model.exceptions.InactiveFinancialAccountException;
import com.resolum.intiva.platform.categories.domain.model.exceptions.InsufficientFundsException;
import com.resolum.intiva.platform.categories.domain.model.exceptions.InvalidTransactionAmountException;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FinancialAccountTests {

    @Test
    void shouldCreateFinancialAccountWithInitialValues() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        assertEquals("Cuenta BCP", account.getName().getName());
        assertEquals(BigDecimal.valueOf(500), account.getCurrentAmount().getAmount());
        assertTrue(account.getIsActive());
        assertEquals(1L, account.getOwnerId());
    }

    @Test
    void shouldApplyIncomeTransaction() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        account.applyTransaction(
                new Money(BigDecimal.valueOf(200), CurrencyCodes.PEN),
                TransactionTypes.INCOME
        );

        assertEquals(BigDecimal.valueOf(700), account.getCurrentAmount().getAmount());
    }

    @Test
    void shouldApplyExpenseTransaction() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        account.applyTransaction(
                new Money(BigDecimal.valueOf(150), CurrencyCodes.PEN),
                TransactionTypes.EXPENSE
        );

        assertEquals(BigDecimal.valueOf(350), account.getCurrentAmount().getAmount());
    }

    @Test
    void shouldThrowInactiveFinancialAccountException_whenAccountIsInactive() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );
        account.deactivate();

        assertThrows(InactiveFinancialAccountException.class, () ->
                account.applyTransaction(
                        new Money(BigDecimal.valueOf(100), CurrencyCodes.PEN),
                        TransactionTypes.EXPENSE
                )
        );
    }

    @Test
    void shouldThrowInvalidTransactionAmountException_whenAmountIsZero() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        assertThrows(InvalidTransactionAmountException.class, () ->
                account.applyTransaction(
                        new Money(BigDecimal.ZERO, CurrencyCodes.PEN),
                        TransactionTypes.EXPENSE
                )
        );
    }

    @Test
    void shouldThrowInvalidTransactionAmountException_whenAmountIsNegative() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        assertThrows(InvalidTransactionAmountException.class, () ->
                account.applyTransaction(
                        new Money(BigDecimal.valueOf(-50), CurrencyCodes.PEN),
                        TransactionTypes.EXPENSE
                )
        );
    }

    @Test
    void shouldThrowInsufficientFundsException_whenExpenseExceedsBalance() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(100), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        assertThrows(InsufficientFundsException.class, () ->
                account.applyTransaction(
                        new Money(BigDecimal.valueOf(200), CurrencyCodes.PEN),
                        TransactionTypes.EXPENSE
                )
        );
    }

    @Test
    void shouldDeactivateAndActivateAccount() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        account.deactivate();
        assertFalse(account.getIsActive());

        account.activate();
        assertTrue(account.getIsActive());
    }

    @Test
    void shouldChangeName() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        account.changeName(new AccountName("Nueva Cuenta"));

        assertEquals("Nueva Cuenta", account.getName().getName());
    }

    @Test
    void shouldApplyMultipleTransactions() {
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(1000), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        account.applyTransaction(
                new Money(BigDecimal.valueOf(200), CurrencyCodes.PEN),
                TransactionTypes.INCOME
        );
        account.applyTransaction(
                new Money(BigDecimal.valueOf(300), CurrencyCodes.PEN),
                TransactionTypes.EXPENSE
        );
        account.applyTransaction(
                new Money(BigDecimal.valueOf(100), CurrencyCodes.PEN),
                TransactionTypes.INCOME
        );

        assertEquals(BigDecimal.valueOf(1000), account.getCurrentAmount().getAmount());
    }
}

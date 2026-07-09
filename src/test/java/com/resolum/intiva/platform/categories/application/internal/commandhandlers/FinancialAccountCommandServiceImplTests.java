package com.resolum.intiva.platform.categories.application.internal.commandhandlers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateDefaultFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.commands.CreateFinancialAccountTransaction;
import com.resolum.intiva.platform.categories.domain.model.commands.UpdateFinancialAccountCommand;
import com.resolum.intiva.platform.categories.domain.model.entities.CashAccount;
import com.resolum.intiva.platform.categories.domain.model.entities.CreditCardAccount;
import com.resolum.intiva.platform.categories.domain.model.entities.DebitCardAccount;
import com.resolum.intiva.platform.categories.domain.model.entities.WalletAccount;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.FinancialAccountRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FinancialAccountCommandServiceImplTests {

    private final FinancialAccountRepository repository = mock(FinancialAccountRepository.class);
    private final FinancialAccountCommandServiceImpl service = new FinancialAccountCommandServiceImpl(repository);

    @Test
    void handleCreate_shouldCreateWalletAccount() {
        var command = new CreateFinancialAccountCommand(
                "Mi Billetera", "WALLET", "PEN", null, BigDecimal.valueOf(500), null, 1L
        );
        when(repository.save(any(FinancialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(command);

        assertInstanceOf(WalletAccount.class, result);
        assertEquals("Mi Billetera", result.getName().getName());
        assertEquals(BigDecimal.valueOf(500), result.getCurrentAmount().getAmount());
        assertTrue(result.getIsActive());
        assertEquals(1L, result.getOwnerId());
        verify(repository).save(any(FinancialAccount.class));
    }

    @Test
    void handleCreate_shouldCreateDebitCardAccount() {
        var command = new CreateFinancialAccountCommand(
                "Debito BCP", "DEBITCARD", "PEN", null, BigDecimal.valueOf(1000), "BCP", 1L
        );
        when(repository.save(any(FinancialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(command);

        assertInstanceOf(DebitCardAccount.class, result);
        assertEquals("Debito BCP", result.getName().getName());
        verify(repository).save(any(FinancialAccount.class));
    }

    @Test
    void handleCreate_shouldCreateCreditCardAccount() {
        var command = new CreateFinancialAccountCommand(
                "Credito BCP", "CREDITCARD", "PEN", BigDecimal.valueOf(5000), BigDecimal.ZERO, "BCP", 1L
        );
        when(repository.save(any(FinancialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(command);

        assertInstanceOf(CreditCardAccount.class, result);
        assertEquals("Credito BCP", result.getName().getName());
        verify(repository).save(any(FinancialAccount.class));
    }

    @Test
    void handleCreate_shouldThrow_whenDebitCardMissingInstitution() {
        var command = new CreateFinancialAccountCommand(
                "Debito", "DEBITCARD", "PEN", null, BigDecimal.valueOf(1000), null, 1L
        );

        assertThrows(IllegalArgumentException.class, () -> service.handle(command));
        verify(repository, never()).save(any(FinancialAccount.class));
    }

    @Test
    void handleCreate_shouldThrow_whenCreditCardMissingCreditLimit() {
        var command = new CreateFinancialAccountCommand(
                "Credito", "CREDITCARD", "PEN", null, BigDecimal.ZERO, "BCP", 1L
        );

        assertThrows(IllegalArgumentException.class, () -> service.handle(command));
        verify(repository, never()).save(any(FinancialAccount.class));
    }

    @Test
    void handleCreate_shouldThrow_whenCreditCardMissingInstitution() {
        var command = new CreateFinancialAccountCommand(
                "Credito", "CREDITCARD", "PEN", BigDecimal.valueOf(5000), BigDecimal.ZERO, null, 1L
        );

        assertThrows(IllegalArgumentException.class, () -> service.handle(command));
        verify(repository, never()).save(any(FinancialAccount.class));
    }

    @Test
    void handleCreate_shouldThrow_whenUnknownAccountType() {
        var command = new CreateFinancialAccountCommand(
                "Invalido", "INVALID", "PEN", null, BigDecimal.ZERO, null, 1L
        );

        assertThrows(IllegalArgumentException.class, () -> service.handle(command));
        verify(repository, never()).save(any(FinancialAccount.class));
    }

    @Test
    void handleUpdate_shouldUpdateName() {
        var account = buildDebitAccount("Cuenta Original", 1L);
        ReflectionTestUtils.setField(account, "id", 10L, Long.class);
        when(repository.findById(10L)).thenReturn(Optional.of(account));
        when(repository.save(any(FinancialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(new UpdateFinancialAccountCommand(10L, "Cuenta Actualizada", null, null));

        assertEquals("Cuenta Actualizada", result.getName().getName());
        assertTrue(result.getIsActive());
        verify(repository).save(any(FinancialAccount.class));
    }

    @Test
    void handleUpdate_shouldDeactivateAccount() {
        var account = buildDebitAccount("Cuenta", 1L);
        ReflectionTestUtils.setField(account, "id", 10L, Long.class);
        when(repository.findById(10L)).thenReturn(Optional.of(account));
        when(repository.save(any(FinancialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(new UpdateFinancialAccountCommand(10L, null, false, null));

        assertFalse(result.getIsActive());
        verify(repository).save(any(FinancialAccount.class));
    }

    @Test
    void handleUpdate_shouldActivateAccount() {
        var account = buildDebitAccount("Cuenta", 1L);
        ReflectionTestUtils.setField(account, "id", 10L, Long.class);
        account.deactivate();
        when(repository.findById(10L)).thenReturn(Optional.of(account));
        when(repository.save(any(FinancialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = service.handle(new UpdateFinancialAccountCommand(10L, null, true, null));

        assertTrue(result.getIsActive());
        verify(repository).save(any(FinancialAccount.class));
    }

    @Test
    void handleUpdate_shouldThrow_whenAccountNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.handle(new UpdateFinancialAccountCommand(999L, "Nuevo", null, null))
        );
        verify(repository, never()).save(any(FinancialAccount.class));
    }

    @Test
    void handleCreateDefault_shouldCreateCashAccount() {
        when(repository.save(any(FinancialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        service.handle(new CreateDefaultFinancialAccountCommand(1L));

        verify(repository).save(any(CashAccount.class));
    }

    @Test
    void handleTransaction_shouldApplyExpense() {
        var account = buildDebitAccount("Cuenta", 1L);
        ReflectionTestUtils.setField(account, "id", 10L, Long.class);
        account.updateCurrentAmount(new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN));
        when(repository.findByIdForUpdate(10L)).thenReturn(Optional.of(account));
        when(repository.save(any(FinancialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        service.handle(new CreateFinancialAccountTransaction(
                10L, BigDecimal.valueOf(200), "PEN", "EXPENSE", OwnerTypes.INDIVIDUAL, null
        ));

        verify(repository).save(any(FinancialAccount.class));
    }

    @Test
    void handleTransaction_shouldApplyIncome() {
        var account = buildDebitAccount("Cuenta", 1L);
        ReflectionTestUtils.setField(account, "id", 10L, Long.class);
        account.updateCurrentAmount(new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN));
        when(repository.findByIdForUpdate(10L)).thenReturn(Optional.of(account));
        when(repository.save(any(FinancialAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        service.handle(new CreateFinancialAccountTransaction(
                10L, BigDecimal.valueOf(300), "PEN", "INCOME", OwnerTypes.INDIVIDUAL, null
        ));

        verify(repository).save(any(FinancialAccount.class));
    }

    @Test
    void handleTransaction_shouldThrow_whenAccountNotFound() {
        when(repository.findByIdForUpdate(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.handle(new CreateFinancialAccountTransaction(
                        999L, BigDecimal.valueOf(100), "PEN", "EXPENSE", OwnerTypes.INDIVIDUAL, null
                ))
        );
        verify(repository, never()).save(any(FinancialAccount.class));
    }

    private DebitCardAccount buildDebitAccount(String name, Long ownerId) {
        return new DebitCardAccount(
                new AccountName(name),
                new Money(BigDecimal.ZERO, CurrencyCodes.PEN),
                new Institution("BCP"),
                ownerId
        );
    }
}

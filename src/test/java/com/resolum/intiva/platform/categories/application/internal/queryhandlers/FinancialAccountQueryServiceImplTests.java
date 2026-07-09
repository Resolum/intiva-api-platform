package com.resolum.intiva.platform.categories.application.internal.queryhandlers;

import com.resolum.intiva.platform.categories.domain.model.aggregates.FinancialAccount;
import com.resolum.intiva.platform.categories.domain.model.entities.DebitCardAccount;
import com.resolum.intiva.platform.categories.domain.model.queries.GetAllFinancialAccountsByOwnerId;
import com.resolum.intiva.platform.categories.domain.model.queries.GetFinancialAccountByIdQuery;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.FinancialAccountRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialAccountQueryServiceImplTests {

    private final FinancialAccountRepository repository = mock(FinancialAccountRepository.class);
    private final FinancialAccountQueryServiceImpl service = new FinancialAccountQueryServiceImpl(repository);

    @Test
    void handleGetAllByOwnerId_shouldReturnList_whenAccountsExist() {
        var ownerId = 1L;
        var accounts = List.of(
                buildAccount(10L, "Cuenta A", ownerId),
                buildAccount(11L, "Cuenta B", ownerId)
        );
        when(repository.findAllByOwnerId(ownerId)).thenReturn(accounts);

        var result = service.handle(new GetAllFinancialAccountsByOwnerId(ownerId));

        assertEquals(2, result.size());
        verify(repository).findAllByOwnerId(ownerId);
    }

    @Test
    void handleGetAllByOwnerId_shouldReturnEmptyList_whenNoAccountsExist() {
        var ownerId = 1L;
        when(repository.findAllByOwnerId(ownerId)).thenReturn(List.of());

        var result = service.handle(new GetAllFinancialAccountsByOwnerId(ownerId));

        assertTrue(result.isEmpty());
        verify(repository).findAllByOwnerId(ownerId);
    }

    @Test
    void handleGetById_shouldReturnAccount_whenFound() {
        var accountId = 10L;
        var account = buildAccount(accountId, "Cuenta Test", 1L);
        when(repository.findById(accountId)).thenReturn(Optional.of(account));

        var result = service.handle(new GetFinancialAccountByIdQuery(accountId));

        assertTrue(result.isPresent());
        assertEquals(accountId, result.get().getId());
        verify(repository).findById(accountId);
    }

    @Test
    void handleGetById_shouldReturnEmpty_whenNotFound() {
        var accountId = 999L;
        when(repository.findById(accountId)).thenReturn(Optional.empty());

        var result = service.handle(new GetFinancialAccountByIdQuery(accountId));

        assertTrue(result.isEmpty());
        verify(repository).findById(accountId);
    }

    private FinancialAccount buildAccount(Long id, String name, Long ownerId) {
        var account = new DebitCardAccount(
                new AccountName(name),
                new Money(BigDecimal.valueOf(100), CurrencyCodes.PEN),
                new Institution("BCP"),
                ownerId
        );
        ReflectionTestUtils.setField(account, "id", id, Long.class);
        return account;
    }
}

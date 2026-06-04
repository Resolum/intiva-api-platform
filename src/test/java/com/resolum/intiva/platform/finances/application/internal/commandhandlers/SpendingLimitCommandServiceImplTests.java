package com.resolum.intiva.platform.finances.application.internal.commandhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalCategoriesService;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.commands.ActivateSpendingLimitCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.CreateSpendingLimitCommand;
import com.resolum.intiva.platform.finances.domain.model.commands.UpdateSpendingLimitPeriodCommand;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.finances.infrastructure.persistence.jpa.repositories.SpendingLimitRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SpendingLimitCommandServiceImpl}.
 */
class SpendingLimitCommandServiceImplTests {

    @Test
    void handleCreate_shouldRejectOverlappingActiveLimitForSameTarget() {
        var repository = mock(SpendingLimitRepository.class);
        var categoriesService = mock(FinancesExternalCategoriesService.class);
        var financialAccountService = mock(FinancesExternalFinancialAccountService.class);
        var service = new SpendingLimitCommandServiceImpl(repository, categoriesService, financialAccountService);
        var existingLimit = buildSpendingLimit(1L, true,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));

        when(categoriesService.existsCategoryById(5L)).thenReturn(true);
        when(repository.findByOwnerIdAndOwnerTypeAndTargetTypeAndTargetIdAndActiveTrue(
                7L, OwnerTypes.INDIVIDUAL, SpendingLimitTargetType.CATEGORY, 5L
        )).thenReturn(List.of(existingLimit));

        var command = new CreateSpendingLimitCommand(
                7L,
                OwnerTypes.INDIVIDUAL,
                SpendingLimitTargetType.CATEGORY,
                5L,
                new Money(BigDecimal.valueOf(400), CurrencyCodes.PEN),
                LocalDate.of(2026, 6, 15),
                LocalDate.of(2026, 7, 15)
        );

        var exception = assertThrows(IllegalArgumentException.class, () -> service.handle(command));

        assertEquals("An active spending limit already exists for the same target and an overlapping period.", exception.getMessage());
        verify(repository, never()).save(any(SpendingLimit.class));
    }

    @Test
    void handleCreate_shouldAllowNonOverlappingActiveLimitForSameTarget() {
        var repository = mock(SpendingLimitRepository.class);
        var categoriesService = mock(FinancesExternalCategoriesService.class);
        var financialAccountService = mock(FinancesExternalFinancialAccountService.class);
        var service = new SpendingLimitCommandServiceImpl(repository, categoriesService, financialAccountService);
        var existingLimit = buildSpendingLimit(1L, true,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));

        when(categoriesService.existsCategoryById(5L)).thenReturn(true);
        when(repository.findByOwnerIdAndOwnerTypeAndTargetTypeAndTargetIdAndActiveTrue(
                7L, OwnerTypes.INDIVIDUAL, SpendingLimitTargetType.CATEGORY, 5L
        )).thenReturn(List.of(existingLimit));
        when(repository.save(any(SpendingLimit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new CreateSpendingLimitCommand(
                7L,
                OwnerTypes.INDIVIDUAL,
                SpendingLimitTargetType.CATEGORY,
                5L,
                new Money(BigDecimal.valueOf(400), CurrencyCodes.PEN),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31)
        );

        var result = service.handle(command);

        assertTrue(result.isPresent());
        verify(repository).save(any(SpendingLimit.class));
    }

    @Test
    void handleUpdatePeriod_shouldRejectOverlapWithAnotherActiveLimit() {
        var repository = mock(SpendingLimitRepository.class);
        var categoriesService = mock(FinancesExternalCategoriesService.class);
        var financialAccountService = mock(FinancesExternalFinancialAccountService.class);
        var service = new SpendingLimitCommandServiceImpl(repository, categoriesService, financialAccountService);
        var currentLimit = buildSpendingLimit(10L, true,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));
        var otherActiveLimit = buildSpendingLimit(11L, true,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));

        when(repository.findById(10L)).thenReturn(Optional.of(currentLimit));
        when(repository.findByOwnerIdAndOwnerTypeAndTargetTypeAndTargetIdAndActiveTrue(
                7L, OwnerTypes.INDIVIDUAL, SpendingLimitTargetType.CATEGORY, 5L
        )).thenReturn(List.of(currentLimit, otherActiveLimit));

        var exception = assertThrows(IllegalArgumentException.class, () -> service.handle(
                new UpdateSpendingLimitPeriodCommand(
                        10L,
                        LocalDate.of(2026, 6, 15),
                        LocalDate.of(2026, 7, 15)
                )
        ));

        assertEquals("An active spending limit already exists for the same target and an overlapping period.", exception.getMessage());
        verify(repository, never()).save(any(SpendingLimit.class));
    }

    @Test
    void handleActivate_shouldRejectActivationWhenAnotherActiveLimitOverlaps() {
        var repository = mock(SpendingLimitRepository.class);
        var categoriesService = mock(FinancesExternalCategoriesService.class);
        var financialAccountService = mock(FinancesExternalFinancialAccountService.class);
        var service = new SpendingLimitCommandServiceImpl(repository, categoriesService, financialAccountService);
        var inactiveLimit = buildSpendingLimit(10L, false,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));
        var activeLimit = buildSpendingLimit(11L, true,
                LocalDate.of(2026, 6, 15), LocalDate.of(2026, 7, 15));

        when(repository.findById(10L)).thenReturn(Optional.of(inactiveLimit));
        when(repository.findByOwnerIdAndOwnerTypeAndTargetTypeAndTargetIdAndActiveTrue(
                7L, OwnerTypes.INDIVIDUAL, SpendingLimitTargetType.CATEGORY, 5L
        )).thenReturn(List.of(activeLimit));

        var exception = assertThrows(IllegalArgumentException.class, () -> service.handle(
                new ActivateSpendingLimitCommand(10L)
        ));

        assertEquals("An active spending limit already exists for the same target and an overlapping period.", exception.getMessage());
        verify(repository, never()).save(any(SpendingLimit.class));
    }

    private SpendingLimit buildSpendingLimit(Long id, boolean active, LocalDate startDate, LocalDate endDate) {
        var spendingLimit = new SpendingLimit(new CreateSpendingLimitCommand(
                7L,
                OwnerTypes.INDIVIDUAL,
                SpendingLimitTargetType.CATEGORY,
                5L,
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                startDate,
                endDate
        ));
        if (!active) {
            spendingLimit.deactivate();
        }
        ReflectionTestUtils.setField(spendingLimit, "id", id, Long.class);
        return spendingLimit;
    }
}

package com.resolum.intiva.platform.finances.application.internal.eventhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalCategoriesService;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalNotificationsService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.commands.CreateSpendingLimitCommand;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitExceededEvent;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitWarningReachedEvent;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SpendingLimitNotificationEventHandler}.
 *
 * <p>These tests focus on the ACL orchestration between finances and communications for warning and exceeded
 * spending-limit notifications.</p>
 */
class SpendingLimitNotificationEventHandlerTests {

    /**
     * Verifies that a warning event is translated into the expected in-app notification request.
     */
    @Test
    void onWarning_shouldRequestWarningNotificationThroughAcl() {
        // Arrange
        var notificationsService = mock(FinancesExternalNotificationsService.class);
        var categoriesService = mock(FinancesExternalCategoriesService.class);
        var financialAccountService = mock(FinancesExternalFinancialAccountService.class);
        var handler = new SpendingLimitNotificationEventHandler(notificationsService, categoriesService, financialAccountService);
        var spendingLimit = buildSpendingLimit(SpendingLimitTargetType.CATEGORY);
        var event = new SpendingLimitWarningReachedEvent(spendingLimit);

        // Act
        handler.on(event);

        // Assert
        verify(notificationsService).createInAppNotification(
                7L,
                "SPENDING_LIMIT_WARNING",
                "SPENDING_LIMIT",
                99L,
                "Limite de gasto proximo a excederse",
                "Tu limite de gasto de categoria del periodo 2026-06-01 al 2026-06-30 esta proximo a excederse."
        );
    }

    /**
     * Verifies that an exceeded event is translated into the expected in-app notification request.
     */
    @Test
    void onExceeded_shouldRequestExceededNotificationThroughAcl() {
        // Arrange
        var notificationsService = mock(FinancesExternalNotificationsService.class);
        var categoriesService = mock(FinancesExternalCategoriesService.class);
        var financialAccountService = mock(FinancesExternalFinancialAccountService.class);
        var handler = new SpendingLimitNotificationEventHandler(notificationsService, categoriesService, financialAccountService);
        var spendingLimit = buildSpendingLimit(SpendingLimitTargetType.FINANCIAL_ACCOUNT);
        var event = new SpendingLimitExceededEvent(spendingLimit);

        // Act
        handler.on(event);

        // Assert
        verify(notificationsService).createInAppNotification(
                7L,
                "SPENDING_LIMIT_EXCEEDED",
                "SPENDING_LIMIT",
                99L,
                "Limite de gasto excedido",
                "Has excedido tu limite de gasto de cuenta financiera del periodo 2026-06-01 al 2026-06-30."
        );
    }

    /**
     * Creates a persisted-like spending limit aggregate used to exercise notification mapping.
     *
     * @param targetType target type used by the aggregate
     * @return spending-limit aggregate with a test identifier
     */
    private SpendingLimit buildSpendingLimit(SpendingLimitTargetType targetType) {
        var spendingLimit = new SpendingLimit(new CreateSpendingLimitCommand(
                7L,
                OwnerTypes.INDIVIDUAL,
                targetType,
                12L,
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30)
        ));
        ReflectionTestUtils.setField(spendingLimit, "id", 99L, Long.class);
        return spendingLimit;
    }
}

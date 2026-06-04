package com.resolum.intiva.platform.finances.application.internal.eventhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalCategoriesService;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalFinancialAccountService;
import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalNotificationsService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitExceededEvent;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitWarningReachedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Finance-side event handler that reacts to spending-limit thresholds and delegates notification creation
 * to the communications bounded context through an ACL facade.
 */
@Service
public class SpendingLimitNotificationEventHandler {

    /**
     * ACL service used to request notification creation in the communications bounded context.
     */
    private final FinancesExternalNotificationsService financesExternalNotificationsService;

    private final FinancesExternalCategoriesService financesExternalCategoriesService;

    private final FinancesExternalFinancialAccountService financesExternalFinancialAccountService;

    /**
     * Creates the event handler with its communications ACL dependency.
     *
     * @param financesExternalNotificationsService communications ACL service
     */
    public SpendingLimitNotificationEventHandler(
            FinancesExternalNotificationsService financesExternalNotificationsService,
            FinancesExternalCategoriesService financesExternalCategoriesService,
            FinancesExternalFinancialAccountService financesExternalFinancialAccountService
    ) {
        this.financesExternalNotificationsService = financesExternalNotificationsService;
        this.financesExternalCategoriesService = financesExternalCategoriesService;
        this.financesExternalFinancialAccountService = financesExternalFinancialAccountService;
    }

    /**
     * Creates an in-app notification when a spending limit reaches its warning threshold.
     *
     * @param event warning threshold event
     */
    @EventListener
    public void on(SpendingLimitWarningReachedEvent event) {
        var spendingLimit = event.getSpendingLimit();
        var type = "SPENDING_LIMIT_WARNING";
        var source = "SPENDING_LIMIT";
        var title = "Limite de gasto proximo a excederse";
        var targetName = resolveTargetName(spendingLimit);
        var message = spendingLimit.buildWarningMessage(spendingLimit, targetName);

        financesExternalNotificationsService.createInAppNotification(
                spendingLimit.getOwnerId(),
                type,
                source,
                spendingLimit.getId(),
                title,
                message
        );
        financesExternalNotificationsService.sendPushNotificationToUser(
                spendingLimit.getOwnerId(),
                type,
                source,
                spendingLimit.getId(),
                title,
                message
        );
    }

    /**
     * Creates an in-app notification when a spending limit is exceeded.
     *
     * @param event exceeded threshold event
     */
    @EventListener
    public void on(SpendingLimitExceededEvent event) {
        var spendingLimit = event.getSpendingLimit();
        var type = "SPENDING_LIMIT_EXCEEDED";
        var source = "SPENDING_LIMIT";
        var title = "Limite de gasto excedido";
        var targetName = resolveTargetName(spendingLimit);
        var message = spendingLimit.buildExceededMessage(spendingLimit, targetName);

        financesExternalNotificationsService.createInAppNotification(
                spendingLimit.getOwnerId(),
                type,
                source,
                spendingLimit.getId(),
                title,
                message
        );
        financesExternalNotificationsService.sendPushNotificationToUser(
                spendingLimit.getOwnerId(),
                type,
                source,
                spendingLimit.getId(),
                title,
                message
        );
    }

    private String resolveTargetName(SpendingLimit spendingLimit) {
        return switch (spendingLimit.getTargetType()) {
            case CATEGORY -> financesExternalCategoriesService
                    .getCategoryNameById(spendingLimit.getTargetId());
            case FINANCIAL_ACCOUNT -> financesExternalFinancialAccountService
                    .getFinancialAccountNameById(spendingLimit.getTargetId());
        };
    }
}

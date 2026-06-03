package com.resolum.intiva.platform.finances.application.internal.eventhandlers;

import com.resolum.intiva.platform.finances.application.internal.outboundservices.acl.FinancesExternalNotificationsService;
import com.resolum.intiva.platform.finances.domain.model.aggregates.SpendingLimit;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitExceededEvent;
import com.resolum.intiva.platform.finances.domain.model.events.SpendingLimitWarningReachedEvent;
import com.resolum.intiva.platform.finances.domain.model.valueobjects.SpendingLimitTargetType;
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

    /**
     * Creates the event handler with its communications ACL dependency.
     *
     * @param financesExternalNotificationsService communications ACL service
     */
    public SpendingLimitNotificationEventHandler(
            FinancesExternalNotificationsService financesExternalNotificationsService
    ) {
        this.financesExternalNotificationsService = financesExternalNotificationsService;
    }

    /**
     * Creates an in-app notification when a spending limit reaches its warning threshold.
     *
     * @param event warning threshold event
     */
    @EventListener
    public void on(SpendingLimitWarningReachedEvent event) {
        var spendingLimit = event.getSpendingLimit();
        financesExternalNotificationsService.createInAppNotification(
                spendingLimit.getOwnerId(),
                "SPENDING_LIMIT_WARNING",
                "SPENDING_LIMIT",
                spendingLimit.getId(),
                "Limite de gasto proximo a excederse",
                buildWarningMessage(spendingLimit)
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
        financesExternalNotificationsService.createInAppNotification(
                spendingLimit.getOwnerId(),
                "SPENDING_LIMIT_EXCEEDED",
                "SPENDING_LIMIT",
                spendingLimit.getId(),
                "Limite de gasto excedido",
                buildExceededMessage(spendingLimit)
        );
    }

    /**
     * Builds the warning message shown to the recipient user.
     *
     * @param spendingLimit spending limit that reached the warning threshold
     * @return warning message body
     */
    private String buildWarningMessage(SpendingLimit spendingLimit) {
        return "Tu limite de gasto de " + describeTarget(spendingLimit)
                + " del periodo " + spendingLimit.getStartDate()
                + " al " + spendingLimit.getEndDate()
                + " esta proximo a excederse.";
    }

    /**
     * Builds the exceeded message shown to the recipient user.
     *
     * @param spendingLimit spending limit that became exceeded
     * @return exceeded message body
     */
    private String buildExceededMessage(SpendingLimit spendingLimit) {
        return "Has excedido tu limite de gasto de " + describeTarget(spendingLimit)
                + " del periodo " + spendingLimit.getStartDate()
                + " al " + spendingLimit.getEndDate() + ".";
    }

    /**
     * Describes the spending-limit target in user-facing text.
     *
     * @param spendingLimit spending limit whose target will be described
     * @return target description
     */
    private String describeTarget(SpendingLimit spendingLimit) {
        return spendingLimit.getTargetType() == SpendingLimitTargetType.CATEGORY
                ? "categoria"
                : "cuenta financiera";
    }
}

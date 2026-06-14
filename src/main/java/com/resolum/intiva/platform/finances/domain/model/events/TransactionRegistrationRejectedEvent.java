package com.resolum.intiva.platform.finances.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

/**
 * Event emitted when a transaction registration is rejected by a business rule.
 */
@Getter
public class TransactionRegistrationRejectedEvent extends ApplicationEvent {

    private final Long recipientUserId;
    private final Long financialAccountId;
    private final BigDecimal amount;
    private final String currencyCode;
    private final String transactionType;
    private final String reason;
    private final String clientOperationId;

    public TransactionRegistrationRejectedEvent(
            Object source,
            Long recipientUserId,
            Long financialAccountId,
            BigDecimal amount,
            String currencyCode,
            String transactionType,
            String reason,
            String clientOperationId
    ) {
        super(source);
        this.recipientUserId = recipientUserId;
        this.financialAccountId = financialAccountId;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.transactionType = transactionType;
        this.reason = reason;
        this.clientOperationId = clientOperationId;
    }
}

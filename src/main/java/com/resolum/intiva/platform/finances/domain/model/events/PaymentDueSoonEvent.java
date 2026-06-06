package com.resolum.intiva.platform.finances.domain.model.events;

import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class PaymentDueSoonEvent extends ApplicationEvent {

    private final Long recurringTransactionId;
    private final Long actorUserId;
    private final Long ownerId;
    private final OwnerTypes ownerType;
    private final String transactionDescription;
    private final BigDecimal amount;
    private final LocalDate endsAt;

    public PaymentDueSoonEvent(
            Object source,
            Long recurringTransactionId,
            Long actorUserId,
            Long ownerId,
            OwnerTypes ownerType,
            String transactionDescription,
            BigDecimal amount,
            LocalDate endsAt
    ) {
        super(source);
        this.recurringTransactionId = recurringTransactionId;
        this.actorUserId = actorUserId;
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.transactionDescription = transactionDescription;
        this.amount = amount;
        this.endsAt = endsAt;
    }
}

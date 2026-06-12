package com.resolum.intiva.platform.finances.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class FamilyTransactionCreatedEvent extends ApplicationEvent {

    private final Long familyId;
    private final Long transactionId;
    private final BigDecimal amount;
    private final String description;
    private final Long actorUserId;

    public FamilyTransactionCreatedEvent(Object source, Long familyId, Long transactionId, BigDecimal amount, String description, Long actorUserId) {
        super(source);
        this.familyId = familyId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.description = description;
        this.actorUserId = actorUserId;
    }
}

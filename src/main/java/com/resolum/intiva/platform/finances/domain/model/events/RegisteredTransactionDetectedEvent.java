package com.resolum.intiva.platform.finances.domain.model.events;

import com.resolum.intiva.platform.shared.domain.valueobjects.CategoryId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

/**
 * Event triggered when a registered transaction is detected, containing details about the transaction type, category, and amount.
 * This triggers a modification of a spending limit associated with the category of the transaction.
 */
@Getter
public class RegisteredTransactionDetectedEvent extends ApplicationEvent {

    // Transaction type (e.g., "EXPENSE", "INCOME", etc.)
    private final String transactionType;

    // Category ID associated with the transaction, used to identify which spent limit to modify.
    private final CategoryId categoryId;

    // Amount of the transaction, which will be used to adjust the spent limit accordingly.
    private final BigDecimal amount;

    // Constructor, getters, and other methods
    public RegisteredTransactionDetectedEvent(Object source, String transactionType, CategoryId categoryId, BigDecimal amount) {
        super(source);
        this.transactionType = transactionType;
        this.categoryId = categoryId;
        this.amount = amount;
    }
}

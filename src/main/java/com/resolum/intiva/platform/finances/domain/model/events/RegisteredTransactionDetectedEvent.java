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

    /**
     * The financial account ID associated with the transaction.
     */
    private final Long financialAccountId;

    /**
     * Amount of the transaction, which will be used to determine the amount of the spent limit adjustment.
     */
    private final BigDecimal amount;

    /**
     * Currency code of the transaction, which will be used to determine the currency of the spent limit adjustment.
     */
    private final String currencyCode;

    /**
     *  Type of transaction (e.g., "EXPENSE", "INCOME", etc.), which will determine how the spent limit is adjusted.
     * For example, if the transaction type is "EXPENSE", the amount will be added to the spent limit, while if it's "INCOME", it might be subtracted from the spent limit.
     */
    private final String transactionType;

    /**
     * Constructor for RegisteredTransactionDetectedEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     * @param financialAccountId the financial account ID associated with the transaction
     * @param transactionType the type of transaction (e.g., "EXPENSE", "INCOME", etc.)
     * @param amount the amount of the transaction
     * @param currencyCode the currency code of the transaction
     */
    public RegisteredTransactionDetectedEvent(Object source, Long financialAccountId, String transactionType, BigDecimal amount, String currencyCode) {
        super(source);
        this.financialAccountId = financialAccountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }
}

package com.resolum.intiva.platform.finances.domain.model.events;

import com.resolum.intiva.platform.shared.domain.valueobjects.CategoryId;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

/**
 * Event published when a transaction is registered and downstream finance handlers need to react to it.
 *
 * <p>The transaction event is used by the financial-account ACL to update balances and carries enough context to
 * let other finance handlers evaluate spending limits for personal and family scopes.</p>
 */
@Getter
public class RegisteredTransactionDetectedEvent extends ApplicationEvent {

    /**
     * The financial account ID associated with the transaction.
     */
    private final Long financialAccountId;

    /**
     * The owner ID associated with the transaction.
     */
    private final Long ownerId;

    /**
     * The owner type associated with the transaction.
     */
    private final OwnerTypes ownerType;

    /**
     * The category ID associated with the transaction.
     */
    private final CategoryId categoryId;

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
     * Creates a new transaction-detected event.
     *
     * @param source event source
     * @param financialAccountId financial account used by the transaction
     * @param ownerId transaction owner id
     * @param ownerType transaction owner scope
     * @param categoryId transaction category
     * @param transactionType transaction type name
     * @param amount transaction amount
     * @param currencyCode transaction currency code
     */
    public RegisteredTransactionDetectedEvent(Object source, Long financialAccountId, Long ownerId, OwnerTypes ownerType, CategoryId categoryId, String transactionType, BigDecimal amount, String currencyCode) {
        super(source);
        this.financialAccountId = financialAccountId;
        this.ownerId = ownerId;
        this.ownerType = ownerType;
        this.categoryId = categoryId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }
}

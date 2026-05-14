package com.resolum.intiva.platform.shared.domain.entities;

import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import com.resolum.intiva.platform.shared.domain.valueobjects.FinancialAccountId;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * TransactionEntry represents a financial transaction entry in the system.
 * It contains details about the transaction such as amount, description, owner, associated financial account.
 *
 * @summary
 * It is used for heriting common properties and behaviors for different types of transaction entries in the finances domain (Transaction & Goal Contribution).
 */
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@MappedSuperclass
public class TransactionEntry extends AuditableAbstractAggregate<TransactionEntry> {

    /**
     * The amount of the transaction, represented as a Money value object. This field is mandatory and must be valid.
     */
    @Embedded
    @Valid
    @AttributeOverride(name = "amount", column = @Column(length = 64, nullable = false))
    protected Money amount;

    /**
     * A brief description of the transaction, providing context and details about the nature of the transaction. This field is mandatory and has a maximum length of 250 characters.
     */
    @AttributeOverride(name = "description", column = @Column(length = 250, nullable = false))
    protected String description;

    /**
     * The identifier of the owner of the transaction, which could be a user or an entity responsible for the transaction. This field is mandatory and must not be null.
     */
    @AttributeOverride(name = "owner_id", column = @Column(nullable = false))
    protected String ownerId;

    /**
     * The identifier of the financial account associated with the transaction, represented as a FinancialAccountId value object. This field is mandatory and must be valid.
     */
    @Embedded
    @Valid
    @AttributeOverride(name = "financial_account_id", column = @Column(nullable = false))
    protected FinancialAccountId financialAccountId;

    /**
     * The identifier of the user who performed the transaction, represented as a UserId value object. This field is mandatory and must be valid.
     */
    @Embedded
    @Valid
    @AttributeOverride(name = "actor_user_id", column = @Column(nullable = false))
    protected UserId actorUserId;

    // Constructors, getters, setters, and other methods
    public TransactionEntry(Money amount, String description, String ownerId, FinancialAccountId financialAccountId, UserId actorUserId) {
        this.amount = amount;
        this.description = description;
        this.ownerId = ownerId;
        this.financialAccountId = financialAccountId;
        this.actorUserId = actorUserId;
    }
}

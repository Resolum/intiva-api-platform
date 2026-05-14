package com.resolum.intiva.platform.finances.domain.model.aggregates;

import com.resolum.intiva.platform.finances.domain.model.valueobjects.TransactionTypes;
import com.resolum.intiva.platform.shared.domain.entities.TransactionEntry;
import com.resolum.intiva.platform.shared.domain.valueobjects.*;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Transaction represents a financial transaction in the system, which can be either an income or an expense. It extends the TransactionEntry class, inheriting common properties such as amount, description, ownerId, financialAccountId, and actorUserId.
 *
 * @summary
 * The Transaction class adds specific properties related to the type of transaction (income or expense) and the category of the transaction.
 */
@Entity
@Getter
@NoArgsConstructor
public class Transaction extends TransactionEntry {

    /**
     * The type of the transaction, indicating whether it is an income or an expense. This field is mandatory and must be a valid TransactionTypes enum value.
     */
    @Enumerated(EnumType.STRING)
    private TransactionTypes transactionType;

    /**
     * The identifier of the category associated with the transaction, represented as a CategoryId value object. This field is optional and can be null if the transaction does not belong to any category.
     */
    @Embedded
    private CategoryId categoryId;

    // Constructor with validation for mandatory fields and business rules.
    public Transaction(@Valid Money amount, String description, String ownerId, @Valid FinancialAccountId financialAccountId, @Valid UserId actorUserId, TransactionTypes transactionType, @Valid CategoryId categoryId) {
        super(amount, description, ownerId, financialAccountId, actorUserId);

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }

        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Owner ID cannot be null or blank");
        }

        this.transactionType = transactionType;
        this.categoryId = categoryId;
    }

    /**
     * Edits the description of the transaction. This method allows updating the description while ensuring that it is not null or blank.
     * @param newDescription The new description for the transaction. It must not be null or blank.
     */
    public void editDescription(String newDescription) {
        if (newDescription == null || newDescription.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        this.description = newDescription;
    }

    /**
     * Edits the amount of the transaction. This method allows updating the amount while ensuring that it is not null and not negative.
     * @param newAmount The new amount for the transaction. It must not be null and must be greater than or equal to zero.
     */
    public void editAmount(Money newAmount) {
        if (newAmount == null || newAmount.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }
        if (InstantComparer.daysBetweenInstantAndNow(getCreatedAt()) > 3) {
            throw new IllegalArgumentException("Amount cannot be edited after 3 days of the transaction creation");
        }
        this.amount = newAmount;
    }

    /**
     * Edits the category of the transaction. This method allows updating the category while ensuring that it is a valid CategoryId value object. The category can also be set to null if the transaction should not belong to any category.
     * @param newCategoryId The new category ID for the transaction. It must be a valid CategoryId value object or null if the transaction should not belong to any category.
     */
    public void editCategory(CategoryId newCategoryId) {
        this.categoryId = newCategoryId;
    }
}

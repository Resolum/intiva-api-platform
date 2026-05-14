package com.resolum.intiva.platform.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing a transaction ID in the finances' domain.
 * @param transactionId the unique identifier for a transaction
 */
@Embeddable
public record TransactionEntryId(String transactionId) {

    // Constructor validation to ensure transaction ID is not null or blank
    public TransactionEntryId {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or blank");
        }
    }

    /**
     * Returns the value of the transaction ID.
     * @return the transaction ID as a string
     */
    public String getValue() {
        return transactionId;
    }
}

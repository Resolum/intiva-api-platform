package com.resolum.intiva.platform.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;

/**
 * Value object representing a financial account ID in the finances' domain.
 * @param financialAccountId the unique identifier for a financial account
 */
@Embeddable
public record FinancialAccountId(Long financialAccountId) {

    // Constructor validation to ensure financial account ID is not null and positive
    public FinancialAccountId {
        if (financialAccountId == null || financialAccountId <= 0) {
            throw new IllegalArgumentException("Financial Account ID must be a positive number");
        }
    }

    /**
     * Returns the value of the financial account ID.
     * @return the financial account ID as a Long
     */
    public Long getValue() {
        return financialAccountId;
    }
}

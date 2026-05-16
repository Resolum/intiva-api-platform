package com.resolum.intiva.platform.shared.domain.valueobjects;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/**
 * Represents a monetary value with an amount and a currency code.
 * This class is immutable and provides methods for basic arithmetic operations and formatting.
 */
@Embeddable
public record Money(BigDecimal amount, CurrencyCodes currencyCode) {

    // Constructor validation to ensure that amount and currency code are not null
    public Money {
        if (amount == null || currencyCode == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount and currency code cannot be null");
        }
    }

    /**
     * Adds the specified amount of money to this money. Both money objects must have the same currency code.
     * @param other the money to add to this money
     * @return a new Money object representing the result of the addition
     */
    public Money add(Money other) {
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot add money with different currency codes");
        }
        return new Money(this.amount.add(other.amount), this.currencyCode);
    }

    /**
     * Subtracts the specified amount of money from this money. Both money objects must have the same currency code, and the amount to subtract cannot be greater than the current amount.
     * @param other the money to subtract from this money
     * @return a new Money object representing the result of the subtraction
     */
    public Money subtract(Money other) {
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot subtract money with different currency codes");
        }
        if (this.amount.compareTo(other.amount) < 0) {
            throw new IllegalArgumentException("Cannot subtract more than the current amount");
        }
        return new Money(this.amount.subtract(other.amount), this.currencyCode);
    }

    /**
     * Formats the money as a string in the format "amount currencyCode", e.g. "100.00 USD".
     * @return the formatted string representation of the money
     */
    public String toStringFormat() {
        return amount + " " + currencyCode.name();
    }

    /**
     * Returns the amount of money as a BigDecimal.
     * @return the amount of money
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the currency code of the money as a string.
     * @return the currency code of the money
     */
    public String getCurrencyCode() {
        return currencyCode.name();
    }
}



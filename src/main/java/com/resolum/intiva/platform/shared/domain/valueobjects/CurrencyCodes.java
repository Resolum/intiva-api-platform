package com.resolum.intiva.platform.shared.domain.valueobjects;

/**
 * Enum representing supported currency codes.
 */
public enum CurrencyCodes {
    USD, EUR, PEN;

    /**
     * Converts a string representation of a currency code to its corresponding enum value.
     * The input string is case-insensitive, and if it does not match any of the defined currency codes, an IllegalArgumentException is thrown.
     *
     * @param value the string representation of the currency code (e.g., "USD", "EUR", "PEN")
     * @return the corresponding CurrencyCodes enum value
     * @throws IllegalArgumentException if the input string does not match any of the defined currency codes
     */
    public static CurrencyCodes fromString(String value) {

        try {
            return CurrencyCodes.valueOf(value.toUpperCase());

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Invalid currency code."
            );
        }
    }
}

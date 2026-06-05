package com.resolum.intiva.platform.categories.domain.model.valueobjects;

import lombok.Getter;

/**
 * Enum representing the type of category, which can be either an expense or an income.
 */
@Getter
public enum CategoryType {
    EXPENSE,
    INCOME
}

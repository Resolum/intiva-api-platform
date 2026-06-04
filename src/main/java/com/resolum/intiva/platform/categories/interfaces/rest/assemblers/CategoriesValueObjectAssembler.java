package com.resolum.intiva.platform.categories.interfaces.rest.assemblers;

import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryType;

/**
 * Assembler class for converting string representations of category types to their corresponding CategoryType enum values.
 */
public class CategoriesValueObjectAssembler {

    /**
     * Converts a string representation of a category type to its corresponding CategoryType enum value.
     *
     * @param categoryType the string representation of the category type
     * @return the corresponding CategoryType enum value
     */
    public static CategoryType toValueObjectFromString(String categoryType) {
        try {
            return CategoryType.valueOf(categoryType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category type: " + categoryType);
        }
    }
}

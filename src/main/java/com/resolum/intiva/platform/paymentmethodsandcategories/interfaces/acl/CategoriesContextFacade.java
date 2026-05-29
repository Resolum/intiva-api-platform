package com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Context facade for categories-related operations.
 */
public interface CategoriesContextFacade {

    /**
     * Check if a category exists by id
     *
     * @param categoryId the category id
     * @return true if a category exists, false otherwise
     */
    boolean existsCategoryById(Long categoryId);

    /**
     * Create a new category
     * @param userId the user id
     */
    void createDefaultCategory(Long userId);

    /**
     * Get a category by id
     * @param categoryId the category id
     * @return the category
     */
    ImmutablePair<String, String> getCategoryColorAndIconById(Long categoryId);
}

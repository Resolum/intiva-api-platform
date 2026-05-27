package com.resolum.intiva.platform.paymentmethodsandcategories.interfaces.acl;

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
}

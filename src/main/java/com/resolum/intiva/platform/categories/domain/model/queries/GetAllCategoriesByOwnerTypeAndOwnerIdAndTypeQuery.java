package com.resolum.intiva.platform.categories.domain.model.queries;

import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryType;

/**
 * Query object for retrieving all categories based on owner type and owner ID.
 *
 * @param ownerType The type of user making the request (e.g., 'individual', 'family') - required.
 * @param ownerId The unique identifier of the user/family making the request - required.
 * @param type The type of categories to retrieve (e.g., 'expense', 'income') - optional.
 */
public record GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
        String ownerType,
        Long ownerId,
        CategoryType type
) {

    public GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery {
        if (ownerType == null || ownerType.isBlank()) {
            throw new IllegalArgumentException("Owner type (ownerType) is required and cannot be blank.");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID (ownerId) is required and cannot be null.");
        }
    }
}

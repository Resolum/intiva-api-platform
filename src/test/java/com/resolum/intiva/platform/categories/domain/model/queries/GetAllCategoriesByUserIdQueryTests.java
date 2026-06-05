package com.resolum.intiva.platform.categories.domain.model.queries;

import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQueryTests {

    // This test verifies that the query stores all provided values correctly.
    // The query is used to retrieve categories by owner type, owner id and category type.
    // This ensures that the query handler receives the correct filtering data.
    @Test
    void shouldStoreAllProvidedValues() {
        var query = new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                "INDIVIDUAL",
                1L,
                CategoryType.EXPENSE
        );

        assertEquals("INDIVIDUAL", query.ownerType());
        assertEquals(1L, query.ownerId());
        assertEquals(CategoryType.EXPENSE, query.type());
    }

    // This test verifies that the query rejects a null owner type.
    // The owner type is required because the system must know if the categories belong to an individual or a family.
    // Without this value, the query would be incomplete and unsafe for category filtering.
    @Test
    void shouldThrowExceptionWhenOwnerTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                        null,
                        1L,
                        CategoryType.EXPENSE
                )
        );
    }

    // This test verifies that the query rejects a blank owner type.
    // A blank owner type does not identify the ownership context of the categories.
    // This prevents ambiguous queries from reaching the query handler.
    @Test
    void shouldThrowExceptionWhenOwnerTypeIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                        " ",
                        1L,
                        CategoryType.EXPENSE
                )
        );
    }

    // This test verifies that the query rejects a null owner id.
    // The owner id is required to know which user's or family's categories should be retrieved.
    // This validation avoids executing category searches without a valid owner identifier.
    @Test
    void shouldThrowExceptionWhenOwnerIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                        "INDIVIDUAL",
                        null,
                        CategoryType.EXPENSE
                )
        );
    }

    // This test verifies that the category type filter can be optional.
    // The type parameter may be null when the application wants to retrieve all category types for an owner.
    // This confirms that the query supports both filtered and unfiltered category searches by type.
    @Test
    void shouldAllowNullCategoryTypeBecauseItIsOptional() {
        var query = new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                "INDIVIDUAL",
                1L,
                null
        );

        assertEquals("INDIVIDUAL", query.ownerType());
        assertEquals(1L, query.ownerId());
        assertNull(query.type());
    }

    // This test verifies that the query supports retrieving only expense categories.
    // Expense categories classify money outflows such as food, transportation or entertainment.
    // This confirms that the query can carry the EXPENSE filter to the query handler.
    @Test
    void shouldSupportExpenseCategoryTypeFilter() {
        var query = new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                "INDIVIDUAL",
                10L,
                CategoryType.EXPENSE
        );

        assertEquals(CategoryType.EXPENSE, query.type());
    }

    // This test verifies that the query supports retrieving only income categories.
    // Income categories classify money inflows such as salary, freelance work or investments.
    // This confirms that the query can carry the INCOME filter to the query handler.
    @Test
    void shouldSupportIncomeCategoryTypeFilter() {
        var query = new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                "INDIVIDUAL",
                10L,
                CategoryType.INCOME
        );

        assertEquals(CategoryType.INCOME, query.type());
    }

    // This test verifies that two queries with the same values are considered equal.
    // Since this query is a Java record, equality should depend on the values of its fields.
    // This ensures predictable behavior when comparing query objects in tests or application logic.
    @Test
    void shouldConsiderQueriesWithSameValuesAsEqual() {
        var firstQuery = new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                "INDIVIDUAL",
                1L,
                CategoryType.EXPENSE
        );

        var secondQuery = new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                "INDIVIDUAL",
                1L,
                CategoryType.EXPENSE
        );

        assertEquals(firstQuery, secondQuery);
    }

    // This test verifies that two queries with different owner ids are not considered equal.
    // Different owner ids represent different category retrieval requests.
    // This is important because each owner should only retrieve their own categories.
    @Test
    void shouldDifferentiateQueriesByOwnerId() {
        var firstQuery = new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                "INDIVIDUAL",
                1L,
                CategoryType.EXPENSE
        );

        var secondQuery = new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                "INDIVIDUAL",
                2L,
                CategoryType.EXPENSE
        );

        assertNotEquals(firstQuery, secondQuery);
    }

    // This test verifies that the query accepts a valid positive owner id.
    // Positive ids represent normal persisted identifiers in the database.
    // This confirms that a valid category retrieval query can be created successfully.
    @Test
    void shouldAcceptPositiveOwnerId() {
        var query = new GetAllCategoriesByOwnerTypeAndOwnerIdAndTypeQuery(
                "INDIVIDUAL",
                100L,
                CategoryType.EXPENSE
        );

        assertTrue(query.ownerId() > 0);
    }
}
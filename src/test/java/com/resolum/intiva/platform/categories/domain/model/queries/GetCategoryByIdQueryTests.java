package com.resolum.intiva.platform.categories.domain.model.queries;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetCategoryByIdQueryTests {

    // This test verifies that the query stores the category id used to retrieve a specific category.
    // The id is required because this query is intended to search for one category only.
    // This ensures that the query handler receives the correct identifier.
    @Test
    void shouldStoreCategoryId() {
        var query = new GetCategoryByIdQuery(1L);

        assertEquals(1L, query.id());
    }

    // This test verifies that the query rejects a null category id.
    // A null id would make it impossible to identify which category should be retrieved.
    // This validation prevents invalid query requests from reaching the application layer.
    @Test
    void shouldThrowExceptionWhenCategoryIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new GetCategoryByIdQuery(null)
        );
    }

    // This test verifies that two category queries with the same id are considered equal.
    // Since this query is a record, equality should be based on the value of its fields.
    // This confirms that two requests for the same category behave as equivalent value objects.
    @Test
    void shouldConsiderQueriesWithSameCategoryIdAsEqual() {
        var firstQuery = new GetCategoryByIdQuery(1L);
        var secondQuery = new GetCategoryByIdQuery(1L);

        assertEquals(firstQuery, secondQuery);
    }

    // This test verifies that two category queries with different ids are not considered equal.
    // Each category id represents a different category retrieval request.
    // This ensures that the application can distinguish between different category lookup operations.
    @Test
    void shouldDifferentiateQueriesByCategoryId() {
        var firstQuery = new GetCategoryByIdQuery(1L);
        var secondQuery = new GetCategoryByIdQuery(2L);

        assertNotEquals(firstQuery, secondQuery);
    }

    // This test verifies that the query accepts a positive category id.
    // Positive ids represent normal persisted identifiers in the database.
    // This confirms that valid category lookup queries can be created successfully.
    @Test
    void shouldAcceptPositiveCategoryId() {
        var query = new GetCategoryByIdQuery(100L);

        assertTrue(query.id() > 0);
    }
}
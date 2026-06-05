package com.resolum.intiva.platform.categories.domain.model.queries;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GetAllCategoriesByUserIdQueryTests {

    // This test verifies that the query stores the user id used to retrieve categories.
    // The userId is the main filter for finding all personal categories belonging to one user.
    // This ensures the query carries the correct value to the query handler.
    @Test
    void shouldStoreUserId() {
        var query = new GetAllCategoriesByUserIdQuery(1L);

        assertEquals(1L, query.userId());
    }

    // This test verifies that the query rejects a null user id.
    // Retrieving categories without a user id would be invalid because the application would not know which user's categories to search.
    // This validation prevents incomplete queries from reaching the query handler.
    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new GetAllCategoriesByUserIdQuery(null)
        );
    }

    // This test verifies the equality behavior of the query record when two queries contain the same user id.
    // Since records are value objects, two queries with the same userId should be considered equivalent.
    // This helps confirm that the query behaves predictably in tests and application logic.
    @Test
    void shouldConsiderQueriesWithSameUserIdAsEqual() {
        var firstQuery = new GetAllCategoriesByUserIdQuery(1L);
        var secondQuery = new GetAllCategoriesByUserIdQuery(1L);

        assertEquals(firstQuery, secondQuery);
    }

    // This test verifies that two queries with different user ids are not considered equal.
    // This is important because each user should retrieve only their own categories.
    // Different user ids must represent different query requests.
    @Test
    void shouldDifferentiateQueriesByUserId() {
        var firstQuery = new GetAllCategoriesByUserIdQuery(1L);
        var secondQuery = new GetAllCategoriesByUserIdQuery(2L);

        assertNotEquals(firstQuery, secondQuery);
    }

    // This test verifies that the query accepts a valid positive user id.
    // A positive id represents a normal persisted user identifier in the system.
    // This confirms that valid query data can be created without throwing exceptions.
    @Test
    void shouldAcceptPositiveUserId() {
        var query = new GetAllCategoriesByUserIdQuery(100L);

        assertTrue(query.userId() > 0);
    }
}
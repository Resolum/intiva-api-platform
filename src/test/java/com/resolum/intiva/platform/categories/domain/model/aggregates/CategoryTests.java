package com.resolum.intiva.platform.categories.domain.model.aggregates;

import com.resolum.intiva.platform.categories.domain.model.commands.CreateCategoryCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTests {

    // This test verifies that a Category aggregate is correctly created from a valid command.
    // It checks that all main fields are assigned from the command and that the category starts as active by default.
    // This is important because the aggregate is the base domain object used later by services, repositories and controllers.
    @Test
    void shouldCreateCategoryFromValidCommand() {
        var command = new CreateCategoryCommand(
                "Food",
                "#FF5733",
                "user",
                1L,
                null
        );

        var category = new Category(command);

        assertNotNull(category);
        assertEquals("Food", category.getName());
        assertEquals("#FF5733", category.getColor());
        assertEquals("USER", category.getOwnerType());
        assertEquals(1L, category.getUserId());
        assertNull(category.getGroupId());
        assertTrue(category.getIsActive());
    }

    // This test verifies that the category name provided in the creation command is stored correctly.
    // The name is one of the most important fields because it identifies the financial category shown to the user.
    // If this value is not stored properly, the category list and financial classification could display incorrect data.
    @Test
    void shouldAssignCategoryNameCorrectly() {
        var command = new CreateCategoryCommand(
                "Transportation",
                "#2196F3",
                "user",
                1L,
                null
        );

        var category = new Category(command);

        assertEquals("Transportation", category.getName());
    }

    // This test verifies that the category color is correctly assigned from the creation command.
    // The color is relevant for the user interface because categories are commonly displayed with visual labels or icons.
    // This ensures that the backend preserves the color selected by the user when creating a category.
    @Test
    void shouldAssignCategoryColorCorrectly() {
        var command = new CreateCategoryCommand(
                "Health",
                "#4CAF50",
                "user",
                1L,
                null
        );

        var category = new Category(command);

        assertEquals("#4CAF50", category.getColor());
    }

    // This test verifies that ownerType is normalized to uppercase when the category is created.
    // The command may receive values such as "user" or "group", but the aggregate stores them as "USER" or "GROUP".
    // This helps keep a consistent format in the database and avoids comparison errors in the application logic.
    @Test
    void shouldConvertOwnerTypeToUpperCase() {
        var command = new CreateCategoryCommand(
                "Education",
                "#9C27B0",
                "user",
                1L,
                null
        );

        var category = new Category(command);

        assertEquals("USER", category.getOwnerType());
    }

    // This test verifies that a personal category is correctly linked to a specific user.
    // When ownerType is "user", the category should store the userId and should not require a groupId.
    // This is important for retrieving only the categories that belong to the authenticated user.
    @Test
    void shouldAssignUserIdForUserCategory() {
        var command = new CreateCategoryCommand(
                "Groceries",
                "#FFC107",
                "user",
                10L,
                null
        );

        var category = new Category(command);

        assertEquals(10L, category.getUserId());
        assertNull(category.getGroupId());
    }

    // This test verifies that a shared category can be linked to a group instead of an individual user.
    // When ownerType is "group", the aggregate should store the groupId and allow userId to be null.
    // This supports the project requirement of managing categories for shared or group-based financial spaces.
    @Test
    void shouldAssignGroupIdForGroupCategory() {
        var command = new CreateCategoryCommand(
                "Shared Rent",
                "#795548",
                "group",
                null,
                20L
        );

        var category = new Category(command);

        assertNull(category.getUserId());
        assertEquals(20L, category.getGroupId());
        assertEquals("GROUP", category.getOwnerType());
    }

    // This test verifies that every new category is active by default after creation.
    // Active categories should be available for normal use in the application unless they are archived later.
    // This confirms that the aggregate starts in a valid state for category listing and selection.
    @Test
    void shouldBeActiveByDefaultWhenCreated() {
        var command = new CreateCategoryCommand(
                "Savings",
                "#00BCD4",
                "user",
                1L,
                null
        );

        var category = new Category(command);

        assertTrue(category.getIsActive());
    }

    // This test verifies the business method used to update the visible details of a category.
    // It checks that both editable fields, name and color, are changed correctly.
    // This is important because users may need to rename a category or change its color after creation.
    @Test
    void shouldUpdateCategoryDetails() {
        var command = new CreateCategoryCommand(
                "Food",
                "#FF5733",
                "user",
                1L,
                null
        );

        var category = new Category(command);

        category.updateDetails("Restaurants", "#E91E63");

        assertEquals("Restaurants", category.getName());
        assertEquals("#E91E63", category.getColor());
    }

    // This test verifies the archive business behavior of the Category aggregate.
    // Instead of deleting the category, the archive method marks it as inactive.
    // This is useful when the application needs to hide old categories without removing their historical financial data.
    @Test
    void shouldArchiveCategory() {
        var command = new CreateCategoryCommand(
                "Entertainment",
                "#673AB7",
                "user",
                1L,
                null
        );

        var category = new Category(command);

        category.archive();

        assertFalse(category.getIsActive());
    }

    // This test verifies that updating category details does not modify ownership information.
    // The name and color can change, but userId, groupId and ownerType must remain stable.
    // This protects the domain rule that a category should not change its owner accidentally during a simple update.
    @Test
    void shouldKeepOwnershipDataWhenUpdatingDetails() {
        var command = new CreateCategoryCommand(
                "Utilities",
                "#607D8B",
                "user",
                5L,
                null
        );

        var category = new Category(command);

        category.updateDetails("Home Services", "#3F51B5");

        assertEquals(5L, category.getUserId());
        assertNull(category.getGroupId());
        assertEquals("USER", category.getOwnerType());
    }
}
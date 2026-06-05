package com.resolum.intiva.platform.categories.domain.model.aggregates;

import com.resolum.intiva.platform.categories.domain.model.commands.CreateCategoryCommand;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryDescription;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryType;
import com.resolum.intiva.platform.shared.domain.valueobjects.Color;
import com.resolum.intiva.platform.shared.domain.valueobjects.Icon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTests {

    // This test verifies that a Category aggregate is correctly created from a valid command.
    // It checks that the main category data is assigned from the command and that the category starts as active.
    // This is important because the aggregate is the main domain object used by services, repositories and controllers.
    @Test
    void shouldCreateCategoryFromValidCommand() {
        var command = new CreateCategoryCommand(
                "Food",
                "INDIVIDUAL",
                1L,
                "Food expenses",
                "#FF5733",
                "food",
                CategoryType.EXPENSE
        );

        var category = new Category(command);

        assertNotNull(category);
        assertEquals("Food", category.getName());
        assertEquals("INDIVIDUAL", category.getOwnerType());
        assertEquals(1L, category.getOwnerId());
        assertEquals(new CategoryDescription("Food expenses"), category.getDescription());
        assertEquals(new Color("#FF5733"), category.getColor());
        assertEquals(new Icon("food"), category.getIcon());
        assertEquals(CategoryType.EXPENSE, category.getType());
        assertTrue(category.getIsActive());
    }

    // This test verifies that the category name provided in the creation command is stored correctly.
    // The name is important because it is the visible label used to classify financial transactions.
    // If the name is not stored correctly, users could see incorrect category information.
    @Test
    void shouldAssignCategoryNameCorrectly() {
        var command = new CreateCategoryCommand(
                "Transportation",
                "INDIVIDUAL",
                1L,
                "Transportation expenses",
                "#2196F3",
                "car",
                CategoryType.EXPENSE
        );

        var category = new Category(command);

        assertEquals("Transportation", category.getName());
    }

    // This test verifies that the owner type is normalized to uppercase when the category is created.
    // The command may receive lowercase values, but the aggregate stores ownerType in uppercase.
    // This helps avoid inconsistent values when comparing or filtering category ownership.
    @Test
    void shouldConvertOwnerTypeToUpperCase() {
        var command = new CreateCategoryCommand(
                "Health",
                "individual",
                1L,
                "Health expenses",
                "#4CAF50",
                "health",
                CategoryType.EXPENSE
        );

        var category = new Category(command);

        assertEquals("INDIVIDUAL", category.getOwnerType());
    }

    // This test verifies that the category is correctly linked to its owner.
    // The ownerId identifies the user or family that owns the category.
    // This is required so the application can retrieve only the categories that belong to a specific owner.
    @Test
    void shouldAssignOwnerIdCorrectly() {
        var command = new CreateCategoryCommand(
                "Groceries",
                "INDIVIDUAL",
                10L,
                "Groceries and supermarket expenses",
                "#FFC107",
                "shopping-cart",
                CategoryType.EXPENSE
        );

        var category = new Category(command);

        assertEquals(10L, category.getOwnerId());
    }

    // This test verifies that the category description is correctly wrapped in the CategoryDescription value object.
    // The aggregate does not store description as a plain String, but as a domain value object.
    // This protects the domain model by keeping category description behavior centralized.
    @Test
    void shouldAssignCategoryDescriptionCorrectly() {
        var command = new CreateCategoryCommand(
                "Education",
                "INDIVIDUAL",
                1L,
                "Courses, books and university materials",
                "#9C27B0",
                "book",
                CategoryType.EXPENSE
        );

        var category = new Category(command);

        assertEquals(
                new CategoryDescription("Courses, books and university materials"),
                category.getDescription()
        );
    }

    // This test verifies that the category color is correctly wrapped in the Color value object.
    // The color is important for the visual representation of categories in the application.
    // This ensures that the selected color is preserved when the category is created.
    @Test
    void shouldAssignCategoryColorCorrectly() {
        var command = new CreateCategoryCommand(
                "Savings",
                "INDIVIDUAL",
                1L,
                "Money reserved for future goals",
                "#00BCD4",
                "savings",
                CategoryType.INCOME
        );

        var category = new Category(command);

        assertEquals(new Color("#00BCD4"), category.getColor());
    }

    // This test verifies that the category icon is correctly wrapped in the Icon value object.
    // Icons help the user identify categories quickly in the interface.
    // This ensures that the backend stores the icon selected during category creation.
    @Test
    void shouldAssignCategoryIconCorrectly() {
        var command = new CreateCategoryCommand(
                "Entertainment",
                "INDIVIDUAL",
                1L,
                "Movies, streaming and games",
                "#673AB7",
                "movie",
                CategoryType.EXPENSE
        );

        var category = new Category(command);

        assertEquals(new Icon("movie"), category.getIcon());
    }

    // This test verifies that a new category is active by default.
    // Active categories should be available for normal use immediately after creation.
    // This confirms that the aggregate starts in a valid state for listing and selection.
    @Test
    void shouldBeActiveByDefaultWhenCreated() {
        var command = new CreateCategoryCommand(
                "Salary",
                "INDIVIDUAL",
                1L,
                "Monthly job income",
                "#4CAF50",
                "briefcase",
                CategoryType.INCOME
        );

        var category = new Category(command);

        assertTrue(category.getIsActive());
    }

    // This test verifies that category details can be updated using the business method.
    // It checks that editable fields such as name, description, color and icon are changed correctly.
    // This is important because users may need to customize their categories after creation.
    @Test
    void shouldUpdateCategoryDetails() {
        var command = new CreateCategoryCommand(
                "Food",
                "INDIVIDUAL",
                1L,
                "Food expenses",
                "#FF5733",
                "food",
                CategoryType.EXPENSE
        );

        var category = new Category(command);

        category.updateDetails(
                "Restaurants",
                "Restaurant expenses",
                "#E91E63",
                "restaurant"
        );

        assertEquals("Restaurants", category.getName());
        assertEquals(new CategoryDescription("Restaurant expenses"), category.getDescription());
        assertEquals(new Color("#E91E63"), category.getColor());
        assertEquals(new Icon("restaurant"), category.getIcon());
    }

    // This test verifies that archiving a category changes its active status to false.
    // Instead of deleting the category, the archive method marks it as inactive.
    // This is useful because historical transactions can keep their category reference.
    @Test
    void shouldArchiveCategory() {
        var command = new CreateCategoryCommand(
                "Utilities",
                "INDIVIDUAL",
                1L,
                "Electricity, water and internet bills",
                "#607D8B",
                "home",
                CategoryType.EXPENSE
        );

        var category = new Category(command);

        category.archive();

        assertFalse(category.getIsActive());
    }
}
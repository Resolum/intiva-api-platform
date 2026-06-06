package com.resolum.intiva.platform.categories.domain.model.commands;

import com.resolum.intiva.platform.categories.domain.model.valueobjects.CategoryType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateCategoryCommandTests {

    // This test verifies that the command stores all valid values received from the application layer.
    // The command works as the input object for creating a category, so it must preserve name, ownerType,
    // ownerId, description, color, icon and type before the aggregate is created.
    // If these values are not stored correctly, the Category aggregate could be created with incorrect information.
    @Test
    void shouldCreateCommandWithValidValues() {
        var command = new CreateCategoryCommand(
                "Food",
                "INDIVIDUAL",
                1L,
                "Food expenses",
                "#FF5733",
                "food",
                CategoryType.EXPENSE
        );

        assertEquals("Food", command.name());
        assertEquals("INDIVIDUAL", command.ownerType());
        assertEquals(1L, command.ownerId());
        assertEquals("Food expenses", command.description());
        assertEquals("#FF5733", command.color());
        assertEquals("food", command.icon());
        assertEquals(CategoryType.EXPENSE, command.type());
    }

    // This test verifies that the command rejects a null category name.
    // A category without a name would not be useful because users could not identify it in the application.
    // This validation prevents incomplete creation requests from reaching the domain aggregate.
    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        null,
                        "INDIVIDUAL",
                        1L,
                        "Food expenses",
                        "#FF5733",
                        "food",
                        CategoryType.EXPENSE
                )
        );
    }

    // This test verifies that the command rejects a blank category name.
    // Even if the value is not null, a blank name does not represent a valid visible category label.
    // This prevents categories with empty names from being created.
    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        " ",
                        "INDIVIDUAL",
                        1L,
                        "Food expenses",
                        "#FF5733",
                        "food",
                        CategoryType.EXPENSE
                )
        );
    }

    // This test verifies that the command rejects a null color value.
    // The color is required because categories use it as part of their visual representation in the application.
    // This ensures that every category has a defined color before reaching the aggregate.
    @Test
    void shouldThrowExceptionWhenColorIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        "Food",
                        "INDIVIDUAL",
                        1L,
                        "Food expenses",
                        null,
                        "food",
                        CategoryType.EXPENSE
                )
        );
    }

    // This test verifies that the command rejects a blank color value.
    // A blank color would be invalid for the user interface and could create inconsistent category displays.
    // This validation ensures that the command only accepts meaningful color values.
    @Test
    void shouldThrowExceptionWhenColorIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        "Food",
                        "INDIVIDUAL",
                        1L,
                        "Food expenses",
                        " ",
                        "food",
                        CategoryType.EXPENSE
                )
        );
    }

    // This test verifies that the command rejects a null ownerType.
    // The ownerType defines whether the category belongs to an individual owner or another ownership context.
    // Without this value, the application would not know how to classify category ownership.
    @Test
    void shouldThrowExceptionWhenOwnerTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        "Food",
                        null,
                        1L,
                        "Food expenses",
                        "#FF5733",
                        "food",
                        CategoryType.EXPENSE
                )
        );
    }

    // This test verifies that the command rejects a blank ownerType.
    // A blank ownerType is invalid because it does not identify the ownership context of the category.
    // This prevents ambiguous ownership data from entering the domain layer.
    @Test
    void shouldThrowExceptionWhenOwnerTypeIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        "Food",
                        " ",
                        1L,
                        "Food expenses",
                        "#FF5733",
                        "food",
                        CategoryType.EXPENSE
                )
        );
    }

    // This test verifies that the command supports an expense category type.
    // Expense categories are used to classify money outflows such as food, transport or entertainment.
    // This confirms that the command can carry the correct type for expense-related category creation.
    @Test
    void shouldSupportExpenseCategoryType() {
        var command = new CreateCategoryCommand(
                "Transportation",
                "INDIVIDUAL",
                15L,
                "Transportation expenses",
                "#2196F3",
                "car",
                CategoryType.EXPENSE
        );

        assertEquals(CategoryType.EXPENSE, command.type());
        assertEquals("Transportation", command.name());
        assertEquals(15L, command.ownerId());
    }

    // This test verifies that the command supports an income category type.
    // Income categories are used to classify money inflows such as salary, freelance work or investments.
    // This confirms that the same command can be used for income category creation.
    @Test
    void shouldSupportIncomeCategoryType() {
        var command = new CreateCategoryCommand(
                "Salary",
                "INDIVIDUAL",
                20L,
                "Monthly job income",
                "#4CAF50",
                "briefcase",
                CategoryType.INCOME
        );

        assertEquals(CategoryType.INCOME, command.type());
        assertEquals("Salary", command.name());
        assertEquals(20L, command.ownerId());
    }

    // This test verifies the default equality behavior of Java records.
    // Two commands with exactly the same values should be considered equal.
    // This is useful because commands are immutable value objects and can be compared safely in tests.
    @Test
    void shouldConsiderCommandsWithSameValuesAsEqual() {
        var firstCommand = new CreateCategoryCommand(
                "Food",
                "INDIVIDUAL",
                1L,
                "Food expenses",
                "#FF5733",
                "food",
                CategoryType.EXPENSE
        );

        var secondCommand = new CreateCategoryCommand(
                "Food",
                "INDIVIDUAL",
                1L,
                "Food expenses",
                "#FF5733",
                "food",
                CategoryType.EXPENSE
        );

        assertEquals(firstCommand, secondCommand);
    }
}
package com.resolum.intiva.platform.categories.domain.model.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateCategoryCommandTests {

    // This test verifies that the command stores all valid values received from the application layer.
    // The command works as the input object for creating a category, so it must preserve name, color, ownerType, userId and groupId.
    // If these values are not stored correctly, the aggregate could be created with incorrect information.
    @Test
    void shouldCreateCommandWithValidValues() {
        var command = new CreateCategoryCommand(
                "Food",
                "#FF5733",
                "user",
                1L,
                null
        );

        assertEquals("Food", command.name());
        assertEquals("#FF5733", command.color());
        assertEquals("user", command.ownerType());
        assertEquals(1L, command.userId());
        assertNull(command.groupId());
    }

    // This test verifies that the command rejects a null category name.
    // A category without a name would not be useful in the application because users could not identify it.
    // This validation protects the domain from receiving incomplete creation requests.
    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        null,
                        "#FF5733",
                        "user",
                        1L,
                        null
                )
        );
    }

    // This test verifies that the command rejects a blank category name.
    // Even if the name is not null, a blank value should not be accepted because it does not represent a valid category.
    // This prevents categories with empty visible labels from being created.
    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        " ",
                        "#FF5733",
                        "user",
                        1L,
                        null
                )
        );
    }

    // This test verifies that the command rejects a null color value.
    // The color is required because categories use it as part of their visual representation in the application.
    // This ensures every category has a defined color before reaching the aggregate.
    @Test
    void shouldThrowExceptionWhenColorIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        "Food",
                        null,
                        "user",
                        1L,
                        null
                )
        );
    }

    // This test verifies that the command rejects a blank color value.
    // A blank color would be invalid for the user interface and could cause inconsistent visual behavior.
    // This validation ensures that the command only accepts meaningful color values.
    @Test
    void shouldThrowExceptionWhenColorIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        "Food",
                        " ",
                        "user",
                        1L,
                        null
                )
        );
    }

    // This test verifies that the command rejects a null ownerType.
    // The ownerType defines whether the category belongs to a user or a group, so it is required for ownership logic.
    // Without this value, the application would not know how to filter or assign the category.
    @Test
    void shouldThrowExceptionWhenOwnerTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        "Food",
                        "#FF5733",
                        null,
                        1L,
                        null
                )
        );
    }

    // This test verifies that the command rejects a blank ownerType.
    // A blank ownerType is invalid because it does not identify whether the category belongs to a user or a group.
    // This prevents ambiguous ownership data from entering the domain layer.
    @Test
    void shouldThrowExceptionWhenOwnerTypeIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateCategoryCommand(
                        "Food",
                        "#FF5733",
                        " ",
                        1L,
                        null
                )
        );
    }

    // This test verifies that the command supports categories owned by an individual user.
    // In this scenario, userId should have a value and groupId can remain null.
    // This is the most common case when a user manages personal expense categories.
    @Test
    void shouldSupportUserOwnedCategory() {
        var command = new CreateCategoryCommand(
                "Transportation",
                "#2196F3",
                "user",
                15L,
                null
        );

        assertEquals("user", command.ownerType());
        assertEquals(15L, command.userId());
        assertNull(command.groupId());
    }

    // This test verifies that the command supports categories owned by a group.
    // In this scenario, groupId should have a value and userId can remain null.
    // This allows the application to create shared categories for group expenses.
    @Test
    void shouldSupportGroupOwnedCategory() {
        var command = new CreateCategoryCommand(
                "Shared Expenses",
                "#4CAF50",
                "group",
                null,
                8L
        );

        assertEquals("group", command.ownerType());
        assertNull(command.userId());
        assertEquals(8L, command.groupId());
    }

    // This test verifies the default equality behavior of Java records.
    // Two commands with exactly the same values should be considered equal.
    // This is useful because commands are immutable value objects and can be compared safely in tests or application logic.
    @Test
    void shouldConsiderCommandsWithSameValuesAsEqual() {
        var firstCommand = new CreateCategoryCommand(
                "Food",
                "#FF5733",
                "user",
                1L,
                null
        );

        var secondCommand = new CreateCategoryCommand(
                "Food",
                "#FF5733",
                "user",
                1L,
                null
        );

        assertEquals(firstCommand, secondCommand);
    }
}
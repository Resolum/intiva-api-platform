package com.resolum.intiva.platform.household.domain.model.aggregates;

import com.resolum.intiva.platform.household.domain.model.commands.CreateFamilyCommand;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyStatus;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FamilyTests {

    private Family buildFamily() {
        return new Family(new CreateFamilyCommand("Mi Familia", "Grupo familiar principal", new UserId(212342432L)));
    }

    @Test
    void create_shouldCreateFamily_whenParametersAreValid() {
        // Arrange
        var command = new CreateFamilyCommand("Mi Familia", "Descripción", new UserId(212342432L));

        // Act
        var family = new Family(command);

        // Assert
        assertNotNull(family);
        assertEquals("Mi Familia", family.getName());
        assertEquals("Descripción", family.getDescription());
        assertEquals(new UserId(212342432L), family.getOwnerId());
        assertEquals(FamilyStatus.ACTIVE, family.getStatus());
        assertNotNull(family.getResourcesUsage());
        assertEquals(0, family.getResourcesUsage().membersActive());
    }

    @Test
    void create_shouldCreateFamily_whenDescriptionIsNull() {
        // Arrange
        var command = new CreateFamilyCommand("Mi Familia", null, new UserId(212342432L));

        // Act
        var family = new Family(command);

        // Assert
        assertNotNull(family);
        assertNull(family.getDescription());
    }
    @Test
    void disolve_shouldSetStatusToDisolved_whenFamilyIsActive() {
        // Arrange
        var family = buildFamily();

        // Act
        family.disolve();

        // Assert
        assertEquals(FamilyStatus.DISOLVED, family.getStatus());
    }
    @Test
    void canInviteMembers_shouldReturnTrue_whenFamilyIsActive() {
        // Arrange
        var family = buildFamily();

        // Act & Assert
        assertTrue(family.canInviteMembers());
    }
    @Test
    void canInviteMembers_shouldReturnFalse_whenFamilyIsDisolved() {
        // Arrange
        var family = buildFamily();
        family.disolve();

        // Act & Assert
        assertFalse(family.canInviteMembers());
    }
    @Test
    void canSetSpendingLimits_shouldReturnTrue_whenFamilyIsActive() {
        // Arrange
        var family = buildFamily();

        // Act & Assert
        assertTrue(family.canSetSpendingLimits());
    }
    @Test
    void canSetSpendingLimits_shouldReturnFalse_whenFamilyIsDisolved() {
        // Arrange
        var family = buildFamily();
        family.disolve();

        // Act & Assert
        assertFalse(family.canSetSpendingLimits());
    }
    @Test
    void canSetSavingsGoals_shouldReturnTrue_whenFamilyIsActive() {
        // Arrange
        var family = buildFamily();

        // Act & Assert
        assertTrue(family.canSetSavingsGoals());
    }

    @Test
    void canSetSavingsGoals_shouldReturnFalse_whenFamilyIsDisolved() {
        // Arrange
        var family = buildFamily();
        family.disolve();

        // Act & Assert
        assertFalse(family.canSetSavingsGoals());
    }
}

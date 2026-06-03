package com.resolum.intiva.platform.savings.domain.model.aggregates;

import com.resolum.intiva.platform.savings.domain.model.entities.GoalContribution;
import com.resolum.intiva.platform.savings.domain.model.valueobjects.SavingGoalStatus;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.OwnerTypes;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SavingGoal aggregate.
 * Covers creation, state transitions, contributions, editing restrictions,
 * and time-based modification rules enforced by the deadline.
 */
public class SavingGoalTests {
    private SavingGoal buildIndividualGoal(Instant deadline) {
        return new SavingGoal(
                OwnerTypes.INDIVIDUAL,
                1L,
                null,
                "Vacaciones",
                new Money(BigDecimal.ZERO, CurrencyCodes.PEN),
                new Money(BigDecimal.valueOf(1000), CurrencyCodes.PEN),
                "Meta de ahorro para vacaciones",
                Instant.now(),
                deadline,
                1L
        );
    }

    private SavingGoal buildFamilyGoal(Instant deadline) {
        return new SavingGoal(
                OwnerTypes.FAMILY,
                null,
                "group-abc",
                "Fondo familiar",
                new Money(BigDecimal.ZERO, CurrencyCodes.PEN),
                new Money(BigDecimal.valueOf(5000), CurrencyCodes.PEN),
                "Meta grupal",
                Instant.now(),
                deadline,
                2L
        );
    }
    /**
     * Verifies that a SavingGoal for an individual user is created successfully
     * with the initial status set to INPROGRESS.
     */
    @Test
    void create_shouldCreateSavingGoal_whenParametersAreValid() {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS);

        // Act
        var goal = buildIndividualGoal(deadline);

        // Assert
        assertNotNull(goal);
        assertEquals(SavingGoalStatus.INPROGRESS, goal.getStatus());
        assertEquals(BigDecimal.ZERO, goal.getCurrentAmount().amount());
    }

    /**
     * Verifies that a SavingGoal for a family group is created successfully
     * with the ownerId set and actorUserId null.
     */
    @Test
    void create_shouldCreateFamilySavingGoal_whenOwnerTypeIsFamily() {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS);

        // Act
        var goal = buildFamilyGoal(deadline);

        // Assert
        assertNotNull(goal);
        assertEquals(OwnerTypes.FAMILY, goal.getOwnerType());
        assertEquals("group-abc", goal.getOwnerId());
        assertNull(goal.getActorUserId());
    }

    /**
     * Verifies that completes() transitions the status to COMPLETED
     * and sets completedAt.
     */
    @Test
    void completes_shouldSetStatusCompleted_whenGoalIsInProgress() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(30, ChronoUnit.DAYS));

        // Act
        goal.completes();

        // Assert
        assertEquals(SavingGoalStatus.COMPLETED, goal.getStatus());
        assertNotNull(goal.getCompletedAt());
    }

    /**
     * Verifies that calling completes() on an already completed goal
     * throws IllegalStateException.
     */
    @Test
    void completes_shouldThrowException_whenGoalIsAlreadyCompleted() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(30, ChronoUnit.DAYS));
        goal.completes();

        // Act & Assert
        assertThrows(IllegalStateException.class, goal::completes);
    }

    /**
     * Verifies that uncompletes() transitions a completed goal back to UNCOMPLETED
     * and clears completedAt.
     */
    @Test
    void uncompletes_shouldSetStatusUncompleted_whenGoalIsCompleted() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(30, ChronoUnit.DAYS));
        goal.completes();

        // Act
        goal.uncompletes();

        // Assert
        assertEquals(SavingGoalStatus.UNCOMPLETED, goal.getStatus());
        assertNull(goal.getCompletedAt());
    }

    /**
     * Verifies that calling uncompletes() on a goal already in UNCOMPLETED state
     * throws IllegalStateException.
     */
    @Test
    void uncompletes_shouldThrowException_whenGoalIsAlreadyUncompleted() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(30, ChronoUnit.DAYS));
        goal.completes();
        goal.uncompletes();

        // Act & Assert
        assertThrows(IllegalStateException.class, goal::uncompletes);
    }
    /**
     * Verifies that a valid contribution increases currentAmount correctly.
     */
    @Test
    void contribute_shouldIncreaseCurrentAmount_whenAmountIsValid() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(30, ChronoUnit.DAYS));
        var contribution = new GoalContribution(
                new Money(BigDecimal.valueOf(200), CurrencyCodes.PEN),
                1L,
                null
        );

        // Act
        goal.contribute(contribution);

        // Assert
        assertEquals(BigDecimal.valueOf(200), goal.getCurrentAmount().amount());
    }

    /**
     * Verifies that a contribution that reaches or exceeds the target amount
     * automatically marks the goal as COMPLETED.
     */
    @Test
    void contribute_shouldAutoComplete_whenCurrentAmountReachesTarget() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(30, ChronoUnit.DAYS));
        var contribution = new GoalContribution(
                new Money(BigDecimal.valueOf(1000), CurrencyCodes.PEN),
                1L,
                null
        );

        // Act
        goal.contribute(contribution);

        // Assert
        assertEquals(SavingGoalStatus.COMPLETED, goal.getStatus());
        assertNotNull(goal.getCompletedAt());
    }

    /**
     * Verifies that a contribution with zero amount throws IllegalArgumentException.
     */
    @Test
    void contribute_shouldThrowException_whenAmountIsZero() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(30, ChronoUnit.DAYS));
        var contribution = new GoalContribution(
                new Money(BigDecimal.ZERO, CurrencyCodes.PEN),
                1L,
                null
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> goal.contribute(contribution));
    }

    /**
     * Verifies that constructing a Money with a negative amount throws IllegalArgumentException.
     * Money itself rejects negative values before they can reach contribute().
     */
    @Test
    void contribute_shouldThrowException_whenAmountIsNegative() {
        // Act & Assert — Money constructor rejects negative amounts directly
        assertThrows(IllegalArgumentException.class,
                () -> new Money(BigDecimal.valueOf(-100), CurrencyCodes.PEN));
    }
    /**
     * Verifies that isEditable() returns true when the deadline is in the future.
     */
    @Test
    void isEditable_shouldReturnTrue_whenDeadlineIsInFuture() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(10, ChronoUnit.DAYS));

        // Act & Assert
        assertTrue(goal.isEditable());
    }

    /**
     * Verifies that isEditable() returns false when the deadline is in the past.
     */
    @Test
    void isEditable_shouldReturnFalse_whenDeadlineIsInPast() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().minus(1, ChronoUnit.DAYS));

        // Act & Assert
        assertFalse(goal.isEditable());
    }
    /**
     * Verifies that editDescriptionOrTitle() updates both fields
     * when the deadline has not passed.
     */
    @Test
    void editDescriptionOrTitle_shouldUpdateFields_whenDeadlineIsInFuture() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(30, ChronoUnit.DAYS));

        // Act
        goal.editDescriptionOrTitle("Nueva descripción", "Nuevo título");

        // Assert
        assertEquals("Nueva descripción", goal.getDescription());
        assertEquals("Nuevo título", goal.getTitle());
    }

    /**
     * Verifies that editDescriptionOrTitle() throws IllegalStateException
     * when the deadline has already passed.
     */
    @Test
    void editDescriptionOrTitle_shouldThrowException_whenDeadlineHasPassed() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().minus(1, ChronoUnit.DAYS));

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class,
                () -> goal.editDescriptionOrTitle("desc", "title"));
        assertEquals("Saving goal cannot be modified after its deadline has passed", exception.getMessage());
    }
    /**
     * Verifies that editTargetAmount() updates the target amount
     * when the deadline has not passed.
     */
    @Test
    void editTargetAmount_shouldUpdateAmount_whenDeadlineIsInFuture() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().plus(30, ChronoUnit.DAYS));
        var newTarget = new Money(BigDecimal.valueOf(2000), CurrencyCodes.PEN);

        // Act
        goal.editTargetAmount(newTarget);

        // Assert
        assertEquals(BigDecimal.valueOf(2000), goal.getTargetAmount().amount());
    }

    /**
     * Verifies that editTargetAmount() throws IllegalStateException
     * when the deadline has already passed.
     */
    @Test
    void editTargetAmount_shouldThrowException_whenDeadlineHasPassed() {
        // Arrange
        var goal = buildIndividualGoal(Instant.now().minus(1, ChronoUnit.DAYS));
        var newTarget = new Money(BigDecimal.valueOf(2000), CurrencyCodes.PEN);

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class,
                () -> goal.editTargetAmount(newTarget));
        assertEquals("Saving goal cannot be modified after its deadline has passed", exception.getMessage());
    }
}

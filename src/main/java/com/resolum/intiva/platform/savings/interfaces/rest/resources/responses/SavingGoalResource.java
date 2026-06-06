package com.resolum.intiva.platform.savings.interfaces.rest.resources.responses;

import java.time.Instant;

/**
 * Response resource representing a saving goal.
 * Used as the output payload for saving goal endpoints.
 *
 * @param id            the unique identifier of the saving goal
 * @param ownerType     the type of owner: INDIVIDUAL for personal, FAMILY for group goals
 * @param actorUserId   the ID of the user who created the saving goal
 * @param ownerId       the group ID when ownerType is FAMILY, null for INDIVIDUAL
 * @param title         the name or title of the saving goal
 * @param currentAmount the amount saved so far
 * @param targetAmount  the total amount required to complete the goal
 * @param currencyCode  the currency code used for this goal (e.g. PEN, USD, EUR)
 * @param description   an optional description of the saving goal
 * @param startsAt      the date and time when the saving goal started
 * @param deadline      the target date by which the goal should be completed
 * @param daysRemaining the number of days remaining until the deadline
 * @param status        the current status: INPROGRESS, COMPLETED or UNCOMPLETED
 * @param categoryId    the optional category associated with this saving goal
 * @param createdAt     the date and time when the saving goal was created
 * @param completedAt   the date and time when the goal was completed, null if not yet completed
 */
public record SavingGoalResource(
                Long id,
                String ownerType,
                Long actorUserId,
                String ownerId,
                String title,
                java.math.BigDecimal currentAmount,
                java.math.BigDecimal targetAmount,
                String currencyCode,
                String description,
                Instant startsAt,
                Instant deadline,
                Long daysRemaining,
                String status,
                Long categoryId,
                Instant createdAt,
                Instant completedAt) {
}

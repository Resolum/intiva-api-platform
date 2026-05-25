package com.resolum.intiva.platform.savings.interfaces.rest.resources.requests;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Resource representing a request to create a new saving goal.
 *
 * @param ownerType    the type of owner (INDIVIDUAL or FAMILY)
 * @param actorUserId  the ID of the user creating the goal
 * @param ownerId      the ID of the group or owner
 * @param title        the title of the saving goal
 * @param targetAmount the target amount of money to save
 * @param currencyCode the currency code for the saving goal
 * @param description  the description of the saving goal
 * @param startsAt     the starting date and time
 * @param deadline     the deadline date and time
 * @param categoryId   the ID of the category for the goal
 */
public record CreateSavingGoalResource(
    String ownerType,
    Long actorUserId,
    String ownerId,
    String title,
    BigDecimal targetAmount,
    String currencyCode,
    String description,
    Instant startsAt,
    Instant deadline,
    Long categoryId
) {}

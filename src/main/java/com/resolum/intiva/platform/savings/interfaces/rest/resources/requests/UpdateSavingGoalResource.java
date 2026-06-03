package com.resolum.intiva.platform.savings.interfaces.rest.resources.requests;

import java.math.BigDecimal;

/**
 * Resource representing a request to update an existing saving goal.
 * All fields are optional; only non-null values will be applied.
 * The update is only allowed while the saving goal's deadline has not passed.
 *
 * @param title           the new title (null to keep current value)
 * @param description     the new description (null to keep current value)
 * @param newTargetAmount the new target amount (null to keep current value)
 */
public record UpdateSavingGoalResource(
        String title,
        String description,
        BigDecimal newTargetAmount
) {}

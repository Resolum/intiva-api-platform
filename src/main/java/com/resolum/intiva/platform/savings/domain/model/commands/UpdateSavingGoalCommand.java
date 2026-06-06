package com.resolum.intiva.platform.savings.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to update the details of an existing saving goal.
 * Only allowed while the saving goal's deadline has not passed.
 *
 * @param savingGoalId    the unique identifier of the saving goal to update
 * @param title           the new title (null to keep current value)
 * @param description     the new description (null to keep current value)
 * @param newTargetAmount the new target amount (null to keep current value)
 */
public record UpdateSavingGoalCommand(
        Long savingGoalId,
        String title,
        String description,
        BigDecimal newTargetAmount
) {}

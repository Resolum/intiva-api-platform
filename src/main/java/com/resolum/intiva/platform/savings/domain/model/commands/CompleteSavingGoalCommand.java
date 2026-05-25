package com.resolum.intiva.platform.savings.domain.model.commands;

/**
 * Command to mark a saving goal as completed.
 * Used when the saved amount has reached the target amount or when
 * the goal is manually confirmed as achieved.
 *
 * @param savingGoalId the unique identifier of the saving goal to complete
 */
public record CompleteSavingGoalCommand(Long savingGoalId) {}

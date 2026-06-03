package com.resolum.intiva.platform.savings.domain.model.commands;

/**
 * Command to delete an existing saving goal.
 * Only allowed while the saving goal's deadline has not passed.
 *
 * @param savingGoalId the unique identifier of the saving goal to delete
 */
public record DeleteSavingGoalCommand(Long savingGoalId) {}

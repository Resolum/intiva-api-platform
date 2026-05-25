package com.resolum.intiva.platform.savings.domain.model.commands;

/**
 * Command to revert a saving goal back to an uncompleted state.
 * Used when a previously completed goal needs to be reopened or
 * when the goal did not actually reach its target before the deadline.
 *
 * @param savingGoalId the unique identifier of the saving goal to uncomplete
 */
public record UncompleteSavingGoalCommand(Long savingGoalId) {}

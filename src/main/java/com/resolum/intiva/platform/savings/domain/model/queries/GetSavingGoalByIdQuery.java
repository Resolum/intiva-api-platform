package com.resolum.intiva.platform.savings.domain.model.queries;

/**
 * Query to retrieve a specific saving goal by its unique identifier.
 *
 * @param savingGoalId the unique identifier of the saving goal to retrieve
 */
public record GetSavingGoalByIdQuery(Long savingGoalId) {
}

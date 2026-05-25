package com.resolum.intiva.platform.savings.domain.model.queries;

/**
 * Query to retrieve all saving goals with COMPLETED status belonging to a specific user.
 *
 * @param userId the unique identifier of the user whose completed saving goals are to be retrieved
 */
public record GetAllCompletedSavingGoalsByUserIdQuery(Long userId) {}

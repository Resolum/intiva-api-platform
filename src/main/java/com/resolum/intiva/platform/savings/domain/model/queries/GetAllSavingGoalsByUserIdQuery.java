package com.resolum.intiva.platform.savings.domain.model.queries;

/**
 * Query to retrieve all saving goals belonging to a specific user.
 *
 * @param userId the ID of the user whose saving goals are to be retrieved
 */
public record GetAllSavingGoalsByUserIdQuery(Long userId) {
}

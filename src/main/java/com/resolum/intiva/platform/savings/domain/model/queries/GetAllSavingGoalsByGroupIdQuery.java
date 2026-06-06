package com.resolum.intiva.platform.savings.domain.model.queries;

/**
 * Query to retrieve all saving goals belonging to a specific family or group.
 *
 * @param groupId the ID of the group or family whose saving goals are to be retrieved
 */
public record GetAllSavingGoalsByGroupIdQuery(String groupId) {
}

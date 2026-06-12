package com.resolum.intiva.platform.communications.domain.model.queries;

/**
 * Query used to retrieve all registered notification devices of one user.
 *
 * @param userId owner of the device tokens
 */
public record GetNotificationDevicesByUserIdQuery(Long userId) {
}

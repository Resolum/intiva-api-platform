package com.resolum.intiva.platform.communications.interfaces.rest.controllers;

import com.resolum.intiva.platform.communications.domain.model.queries.GetActiveNotificationDevicesByUserIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationDevicesByUserIdQuery;
import com.resolum.intiva.platform.communications.domain.services.NotificationDeviceQueryService;
import com.resolum.intiva.platform.communications.interfaces.rest.assemblers.NotificationDeviceResourceFromEntityAssembler;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.responses.NotificationDeviceResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageWrapperResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that exposes user-scoped queries for registered device tokens.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Endpoints related to user device token registrations")
public class UserNotificationDevicesController {

    /**
     * Query service used to retrieve device tokens of one user.
     */
    private final NotificationDeviceQueryService notificationDeviceQueryService;

    /**
     * Creates the controller with its query dependency.
     *
     * @param notificationDeviceQueryService device-token query service
     */
    public UserNotificationDevicesController(NotificationDeviceQueryService notificationDeviceQueryService) {
        this.notificationDeviceQueryService = notificationDeviceQueryService;
    }

    /**
     * Retrieves all device-token registrations of one user.
     */
    @GetMapping("/{userId}/notification-devices")
    @Operation(
            summary = "Get user notification devices",
            description = "Retrieves all registered mobile device tokens of the specified user ordered by last update."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device tokens retrieved successfully")
    })
    public ResponseEntity<MessageWrapperResponse<List<NotificationDeviceResource>>> getNotificationDevicesByUserId(
            @PathVariable Long userId
    ) {
        var notificationDevices = notificationDeviceQueryService.handle(new GetNotificationDevicesByUserIdQuery(userId));
        var resources = notificationDevices.stream()
                .map(NotificationDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(new MessageWrapperResponse<>(
                notificationDevices.isEmpty()
                        ? "No notification devices found for the provided user."
                        : "Notification devices retrieved successfully.",
                resources
        ));
    }

    /**
     * Retrieves active device-token registrations of one user.
     */
    @GetMapping("/{userId}/notification-devices/active")
    @Operation(
            summary = "Get active user notification devices",
            description = "Retrieves active mobile device tokens of the specified user ordered by last update."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active device tokens retrieved successfully")
    })
    public ResponseEntity<MessageWrapperResponse<List<NotificationDeviceResource>>> getActiveNotificationDevicesByUserId(
            @PathVariable Long userId
    ) {
        var notificationDevices = notificationDeviceQueryService.handle(new GetActiveNotificationDevicesByUserIdQuery(userId));
        var resources = notificationDevices.stream()
                .map(NotificationDeviceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(new MessageWrapperResponse<>(
                notificationDevices.isEmpty()
                        ? "No active notification devices found for the provided user."
                        : "Active notification devices retrieved successfully.",
                resources
        ));
    }
}

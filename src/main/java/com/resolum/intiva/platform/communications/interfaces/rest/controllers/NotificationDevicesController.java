package com.resolum.intiva.platform.communications.interfaces.rest.controllers;

import com.resolum.intiva.platform.communications.domain.model.queries.GetActiveNotificationDevicesByUserIdQuery;
import com.resolum.intiva.platform.communications.domain.services.NotificationDeviceCommandService;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationDevicesByUserIdQuery;
import com.resolum.intiva.platform.communications.domain.services.NotificationDeviceQueryService;
import com.resolum.intiva.platform.communications.interfaces.rest.assemblers.NotificationDeviceResourceFromEntityAssembler;
import com.resolum.intiva.platform.communications.interfaces.rest.assemblers.RegisterNotificationDeviceCommandFromResourceAssembler;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.requests.RegisterNotificationDeviceResource;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.responses.NotificationDeviceResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageWrapperResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that exposes user-scoped queries for registered device tokens.
 */
@RestController
@RequestMapping(value = "/api/v1/notification-devices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notification Devices", description = "Endpoints related to user device token registrations")
public class NotificationDevicesController {

    /**
     * Query service used to retrieve device tokens of one user.
     */
    private final NotificationDeviceQueryService notificationDeviceQueryService;

    /**
     * Command service used to register and reactivate device tokens.
     */
    private final NotificationDeviceCommandService notificationDeviceCommandService;

    /**
     * Creates the controller with its query dependency.
     *
     * @param notificationDeviceQueryService device-token query service
     */
    public NotificationDevicesController(
            NotificationDeviceQueryService notificationDeviceQueryService,
            NotificationDeviceCommandService notificationDeviceCommandService
    ) {
        this.notificationDeviceQueryService = notificationDeviceQueryService;
        this.notificationDeviceCommandService = notificationDeviceCommandService;
    }

    /**
     * Registers or reactivates one mobile device token for push notifications.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Register notification device",
            description = "Registers or reactivates one mobile device token for push notifications."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device token registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid device token data")
    })
    public ResponseEntity<?> registerNotificationDevice(
            @RequestBody RegisterNotificationDeviceResource resource
    ) {
        try {
            var command = RegisterNotificationDeviceCommandFromResourceAssembler.toCommandFromResource(resource);
            var notificationDevice = notificationDeviceCommandService.handle(command)
                    .orElseThrow(() -> new IllegalStateException("Notification device could not be registered."));
            var response = NotificationDeviceResourceFromEntityAssembler.toResourceFromEntity(notificationDevice);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieves all device-token registrations of one user.
     */
    @GetMapping
    @Operation(
            summary = "Get user notification devices",
            description = "Retrieves all registered mobile device tokens of the specified user ordered by last update."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device tokens retrieved successfully")
    })
    public ResponseEntity<MessageWrapperResponse<List<NotificationDeviceResource>>> getNotificationDevicesByUserId(
            @RequestParam Long userId
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
    @GetMapping("/active")
    @Operation(
            summary = "Get active user notification devices",
            description = "Retrieves active mobile device tokens of the specified user ordered by last update."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active device tokens retrieved successfully")
    })
    public ResponseEntity<MessageWrapperResponse<List<NotificationDeviceResource>>> getActiveNotificationDevicesByUserId(
            @RequestParam Long userId
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

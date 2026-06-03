package com.resolum.intiva.platform.communications.interfaces.rest.controllers;

import com.resolum.intiva.platform.communications.domain.model.commands.DeactivateNotificationDeviceCommand;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationDeviceByIdQuery;
import com.resolum.intiva.platform.communications.domain.services.NotificationDeviceCommandService;
import com.resolum.intiva.platform.communications.domain.services.NotificationDeviceQueryService;
import com.resolum.intiva.platform.communications.interfaces.rest.assemblers.NotificationDeviceResourceFromEntityAssembler;
import com.resolum.intiva.platform.communications.interfaces.rest.assemblers.RegisterNotificationDeviceCommandFromResourceAssembler;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.requests.RegisterNotificationDeviceResource;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.responses.NotificationDeviceResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for registering and maintaining push-notification device tokens.
 */
@RestController
@RequestMapping(value = "/api/v1/notification-devices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notification Devices", description = "Endpoints for mobile device token registration and maintenance")
public class NotificationDevicesController {

    /**
     * Command service used to register and deactivate device tokens.
     */
    private final NotificationDeviceCommandService notificationDeviceCommandService;

    /**
     * Query service used to retrieve registered device tokens by identifier.
     */
    private final NotificationDeviceQueryService notificationDeviceQueryService;

    /**
     * Creates the controller with its command and query dependencies.
     *
     * @param notificationDeviceCommandService device-token command service
     * @param notificationDeviceQueryService device-token query service
     */
    public NotificationDevicesController(
            NotificationDeviceCommandService notificationDeviceCommandService,
            NotificationDeviceQueryService notificationDeviceQueryService
    ) {
        this.notificationDeviceCommandService = notificationDeviceCommandService;
        this.notificationDeviceQueryService = notificationDeviceQueryService;
    }

    /**
     * Registers or reactivates one device token reported by a mobile client.
     */
    @PostMapping
    @Operation(
            summary = "Register a notification device token",
            description = "Registers a new mobile device token or reactivates an existing token for the same user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device token registered successfully", content = @Content(schema = @Schema(implementation = NotificationDeviceResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user id, device token, or platform")
    })
    public ResponseEntity<?> registerNotificationDevice(
            @RequestBody(
                    description = "Device token registration reported by the mobile application.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterNotificationDeviceResource.class))
            )
            @org.springframework.web.bind.annotation.RequestBody RegisterNotificationDeviceResource resource
    ) {
        try {
            var command = RegisterNotificationDeviceCommandFromResourceAssembler.toCommandFromResource(resource);
            var notificationDevice = notificationDeviceCommandService.handle(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(NotificationDeviceResourceFromEntityAssembler.toResourceFromEntity(notificationDevice.get()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(new MessageResource(exception.getMessage()));
        }
    }

    /**
     * Retrieves one registered device token by identifier.
     */
    @GetMapping("/{notificationDeviceId}")
    @Operation(
            summary = "Get notification device by ID",
            description = "Retrieves one registered push-notification device token by its identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device token retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Device token not found")
    })
    public ResponseEntity<NotificationDeviceResource> getNotificationDeviceById(@PathVariable Long notificationDeviceId) {
        var notificationDevice = notificationDeviceQueryService.handle(new GetNotificationDeviceByIdQuery(notificationDeviceId));
        return notificationDevice
                .map(entity -> ResponseEntity.ok(NotificationDeviceResourceFromEntityAssembler.toResourceFromEntity(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deactivates one registered device token.
     */
    @PatchMapping("/{notificationDeviceId}/deactivate")
    @Operation(
            summary = "Deactivate notification device token",
            description = "Deactivates one registered push-notification device token so it is ignored in future deliveries."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device token deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid device identifier")
    })
    public ResponseEntity<?> deactivateNotificationDevice(@PathVariable Long notificationDeviceId) {
        try {
            var notificationDevice = notificationDeviceCommandService.handle(
                    new DeactivateNotificationDeviceCommand(notificationDeviceId)
            );
            return ResponseEntity.ok(NotificationDeviceResourceFromEntityAssembler.toResourceFromEntity(notificationDevice.get()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(new MessageResource(exception.getMessage()));
        }
    }
}

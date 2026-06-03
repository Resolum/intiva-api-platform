package com.resolum.intiva.platform.communications.interfaces.rest.controllers;

import com.resolum.intiva.platform.communications.domain.model.commands.MarkNotificationAsReadCommand;
import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationByIdQuery;
import com.resolum.intiva.platform.communications.domain.services.NotificationCommandService;
import com.resolum.intiva.platform.communications.domain.services.NotificationQueryService;
import com.resolum.intiva.platform.communications.interfaces.rest.assemblers.NotificationResourceFromEntityAssembler;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.responses.NotificationResource;
import com.resolum.intiva.platform.shared.interfaces.rest.resource.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for notification retrieval and status changes by identifier.
 */
@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Endpoints related to persisted in-app notifications")
public class NotificationsController {

    /**
     * Command service used to update notification status.
     */
    private final NotificationCommandService notificationCommandService;

    /**
     * Query service used to retrieve notifications by identifier.
     */
    private final NotificationQueryService notificationQueryService;

    /**
     * Creates the controller with its command and query dependencies.
     *
     * @param notificationCommandService notification command service
     * @param notificationQueryService notification query service
     */
    public NotificationsController(
            NotificationCommandService notificationCommandService,
            NotificationQueryService notificationQueryService
    ) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    /**
     * Retrieves one persisted notification by id.
     *
     * @param notificationId notification identifier
     * @return notification if it exists
     */
    @GetMapping("/{notificationId}")
    @Operation(
            summary = "Get notification by ID",
            description = "Retrieves one persisted in-app notification by its identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationResource> getNotificationById(@PathVariable Long notificationId) {
        var notification = notificationQueryService.handle(new GetNotificationByIdQuery(notificationId));
        return notification
                .map(entity -> ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Marks one persisted notification as read.
     *
     * @param notificationId notification identifier
     * @return updated notification with READ status
     */
    @PatchMapping("/{notificationId}/read")
    @Operation(
            summary = "Mark notification as read",
            description = "Marks one persisted in-app notification as read."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid notification id")
    })
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            var notification = notificationCommandService.handle(new MarkNotificationAsReadCommand(notificationId));
            return ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(notification.get()));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(new MessageResource(exception.getMessage()));
        }
    }
}

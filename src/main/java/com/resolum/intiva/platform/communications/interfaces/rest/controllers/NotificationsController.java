package com.resolum.intiva.platform.communications.interfaces.rest.controllers;

import com.resolum.intiva.platform.communications.domain.model.queries.GetNotificationsByRecipientUserIdQuery;
import com.resolum.intiva.platform.communications.domain.model.queries.GetUnreadNotificationsByRecipientUserIdQuery;
import com.resolum.intiva.platform.communications.domain.services.NotificationQueryService;
import com.resolum.intiva.platform.communications.interfaces.rest.assemblers.NotificationResourceFromEntityAssembler;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.responses.NotificationResource;
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
 * REST controller that exposes recipient-scoped notification queries.
 */
@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Endpoints related to user notifications management")
public class NotificationsController {

    /**
     * Query service used to retrieve notifications for one recipient user.
     */
    private final NotificationQueryService notificationQueryService;

    /**
     * Creates the controller with the query service it needs.
     *
     * @param notificationQueryService notification query service
     */
    public NotificationsController(NotificationQueryService notificationQueryService) {
        this.notificationQueryService = notificationQueryService;
    }

    /**
     * Retrieves all persisted notifications of one user.
     *
     * @param userId recipient user identifier
     * @return notifications belonging to the given user
     */
    @GetMapping
    @Operation(
            summary = "Get user notifications",
            description = "Retrieves all persisted in-app notifications of the specified user ordered by creation date."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    public ResponseEntity<MessageWrapperResponse<List<NotificationResource>>> getNotificationsByUserId(
            @RequestParam Long userId
    ) {
        var notifications = notificationQueryService.handle(new GetNotificationsByRecipientUserIdQuery(userId));
        var resources = notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(new MessageWrapperResponse<>(
                notifications.isEmpty()
                        ? "No notifications found for the provided user."
                        : "Notifications retrieved successfully.",
                resources
        ));
    }

    /**
     * Retrieves unread persisted notifications of one user.
     *
     * @param userId recipient user identifier
     * @return unread notifications belonging to the given user
     */
    @GetMapping("/unread")
    @Operation(
            summary = "Get unread user notifications",
            description = "Retrieves unread persisted in-app notifications of the specified user ordered by creation date."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unread notifications retrieved successfully")
    })
    public ResponseEntity<MessageWrapperResponse<List<NotificationResource>>> getUnreadNotificationsByUserId(
            @RequestParam Long userId
    ) {
        var notifications = notificationQueryService.handle(new GetUnreadNotificationsByRecipientUserIdQuery(userId));
        var resources = notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(new MessageWrapperResponse<>(
                notifications.isEmpty()
                        ? "No unread notifications found for the provided user."
                        : "Unread notifications retrieved successfully.",
                resources
        ));
    }
}

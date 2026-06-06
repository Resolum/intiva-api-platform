package com.resolum.intiva.platform.communications.interfaces.rest.assemblers;

import com.resolum.intiva.platform.communications.domain.model.aggregates.Notification;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.responses.NotificationResource;

/**
 * Maps persisted notification aggregates into REST response resources.
 */
public class NotificationResourceFromEntityAssembler {

    /**
     * Converts one notification aggregate into its REST representation.
     *
     * @param entity notification aggregate
     * @return response resource built from the aggregate
     */
    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(
                entity.getId(),
                entity.getRecipientUserId(),
                entity.getType().name(),
                entity.getSource().name(),
                entity.getSourceId(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getStatus().name(),
                entity.getCreatedAt() == null ? null : entity.getCreatedAt().toString()
        );
    }
}

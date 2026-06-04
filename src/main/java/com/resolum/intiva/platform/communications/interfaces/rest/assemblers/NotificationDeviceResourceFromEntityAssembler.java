package com.resolum.intiva.platform.communications.interfaces.rest.assemblers;

import com.resolum.intiva.platform.communications.domain.model.aggregates.NotificationDevice;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.responses.NotificationDeviceResource;

/**
 * Maps registered device-token aggregates into REST response resources.
 */
public class NotificationDeviceResourceFromEntityAssembler {

    /**
     * Converts one registered device aggregate into its REST representation.
     *
     * @param entity registered device aggregate
     * @return response resource built from the aggregate
     */
    public static NotificationDeviceResource toResourceFromEntity(NotificationDevice entity) {
        return new NotificationDeviceResource(
                entity.getId(),
                entity.getUserId(),
                entity.getDeviceToken(),
                entity.getPlatform(),
                entity.getActive(),
                entity.getCreatedAt() == null ? null : entity.getCreatedAt().toString(),
                entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().toString()
        );
    }
}

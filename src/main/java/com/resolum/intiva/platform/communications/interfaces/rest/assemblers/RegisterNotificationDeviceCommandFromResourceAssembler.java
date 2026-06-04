package com.resolum.intiva.platform.communications.interfaces.rest.assemblers;

import com.resolum.intiva.platform.communications.domain.model.commands.RegisterNotificationDeviceCommand;
import com.resolum.intiva.platform.communications.interfaces.rest.resources.requests.RegisterNotificationDeviceResource;

/**
 * Maps REST request bodies into notification-device registration commands.
 */
public class RegisterNotificationDeviceCommandFromResourceAssembler {

    /**
     * Converts one device-registration REST resource into its application command.
     *
     * @param resource incoming REST request body
     * @return command built from the request body
     */
    public static RegisterNotificationDeviceCommand toCommandFromResource(RegisterNotificationDeviceResource resource) {
        return new RegisterNotificationDeviceCommand(
                resource.userId(),
                resource.deviceToken(),
                resource.platform()
        );
    }
}

package com.resolum.intiva.platform.household.interfaces.rest.assemblers;

import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.interfaces.rest.resources.responses.InvitationResource;

import java.time.LocalDateTime;

/**
 * Assembler that converts an Invitation aggregate into an InvitationResource REST response.
 */
public class InvitationResourceFromEntityAssembler {

    /**
     * Converts an Invitation entity into an InvitationResource.
     *
     * @param entity the Invitation aggregate to convert
     * @return the corresponding InvitationResource
     */
    public static InvitationResource toResourceFromEntity(Invitation entity) {
        return new InvitationResource(
                entity.getId(),
                entity.getToken(),
                entity.getStatus().name(),
                entity.getSentAt().toString(),
                entity.getExpiresAt().toString(),
                entity.getRespondedAt() != null ? entity.getRespondedAt().toString() : null,
                entity.getInvitedBy().getValue(),
                entity.getInvitedForFamily(),
                entity.getUserInvitedId() != null ? entity.getUserInvitedId().getValue() : null,
                entity.getExpiresAt().isBefore(LocalDateTime.now())
        );
    }
}

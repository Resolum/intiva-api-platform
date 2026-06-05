package com.resolum.intiva.platform.communications.application.internal.eventhandlers;

import com.resolum.intiva.platform.communications.interfaces.acl.CommunicationsContextFacade;
import com.resolum.intiva.platform.communications.application.internal.outboundservices.acl.CommunicationsExternalHouseholdService;
import com.resolum.intiva.platform.finances.domain.model.events.FamilyTransactionCreatedEvent;
import com.resolum.intiva.platform.household.domain.model.events.FamilyInvitationSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FamilyEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyEventHandler.class);

    private final CommunicationsContextFacade communicationsContextFacade;
    private final CommunicationsExternalHouseholdService communicationsExternalHouseholdService;

    public FamilyEventHandler(
            CommunicationsContextFacade communicationsContextFacade,
            CommunicationsExternalHouseholdService communicationsExternalHouseholdService
    ) {
        this.communicationsContextFacade = communicationsContextFacade;
        this.communicationsExternalHouseholdService = communicationsExternalHouseholdService;
    }

    @EventListener
    public void on(FamilyTransactionCreatedEvent event) {
        LOGGER.info("Handling FamilyTransactionCreatedEvent: familyId={}, transactionId={}, actorUserId={}",
                event.getFamilyId(), event.getTransactionId(), event.getActorUserId());

        var memberUserIds = communicationsExternalHouseholdService.getActiveFamilyMemberUserIds(event.getFamilyId());

        memberUserIds.stream()
                .filter(userId -> !userId.equals(event.getActorUserId()))
                .forEach(userId -> {
                    communicationsContextFacade.createInAppNotification(
                            userId,
                            "FAMILY_TRANSACTION_REGISTERED",
                            "FAMILY_GROUP",
                            event.getFamilyId(),
                            "Nueva transacción en tu grupo",
                            "Se ha registrado una nueva transacción en tu grupo familiar."
                    );
                    LOGGER.info("Family transaction notification sent to userId={}", userId);
                });
    }

    @EventListener
    public void on(FamilyInvitationSentEvent event) {
        LOGGER.info("Handling FamilyInvitationSentEvent: familyId={}, invitedUserId={}, invitedByUserId={}",
                event.getFamilyId(), event.getInvitedUserId(), event.getInvitedByUserId());

        communicationsContextFacade.createInAppNotification(
                event.getInvitedUserId(),
                "FAMILY_GROUP_INVITATION",
                "FAMILY_GROUP",
                event.getFamilyId(),
                "Tienes una invitación a un grupo familiar",
                "Has sido invitado a un grupo familiar."
        );

        LOGGER.info("Invitation notification sent to userId={}", event.getInvitedUserId());
    }
}

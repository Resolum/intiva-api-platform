package com.resolum.intiva.platform.household.application.internal.eventhandlers;

import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationSource;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationType;
import com.resolum.intiva.platform.communications.interfaces.acl.CommunicationsContextFacade;
import com.resolum.intiva.platform.household.domain.model.aggregates.Family;
import com.resolum.intiva.platform.household.domain.model.events.InvitationRejectedEvent;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
public class InvitationRejectedEventHandler {

    private final FamilyRepository familyRepository;
    private final CommunicationsContextFacade communicationsContextFacade;

    public InvitationRejectedEventHandler(
            FamilyRepository familyRepository,
            CommunicationsContextFacade communicationsContextFacade
    ) {
        this.familyRepository = familyRepository;
        this.communicationsContextFacade = communicationsContextFacade;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(InvitationRejectedEvent event) {
        var rejectorName = event.getRejectorName() == null || event.getRejectorName().isBlank()
                ? "Un usuario"
                : event.getRejectorName();
        var familyName = familyRepository.findById(event.getFamilyId())
                .map(Family::getName)
                .orElse("el grupo");

        try {
            communicationsContextFacade.sendPushNotificationToUser(
                    event.getInviterUserId(),
                    NotificationType.FAMILY_GROUP_INVITATION.name(),
                    NotificationSource.FAMILY_GROUP.name(),
                    event.getFamilyId(),
                    "Invitación rechazada",
                    rejectorName + " rechazó tu invitación al grupo " + familyName
            );
        } catch (Exception exception) {
            log.warn("Invitation rejected push notification could not be sent. invitationId={}, inviterUserId={}",
                    event.getInvitationId(), event.getInviterUserId(), exception);
        }
    }
}

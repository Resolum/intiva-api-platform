package com.resolum.intiva.platform.household.application.internal.eventhandlers;

import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationSource;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationType;
import com.resolum.intiva.platform.communications.interfaces.acl.CommunicationsContextFacade;
import com.resolum.intiva.platform.household.domain.model.aggregates.Family;
import com.resolum.intiva.platform.household.domain.model.events.InvitationAcceptedEvent;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.InvitationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
public class InvitationAcceptedEventHandler {

    private final InvitationRepository invitationRepository;
    private final FamilyRepository familyRepository;
    private final CommunicationsContextFacade communicationsContextFacade;

    public InvitationAcceptedEventHandler(
            InvitationRepository invitationRepository,
            FamilyRepository familyRepository,
            CommunicationsContextFacade communicationsContextFacade
    ) {
        this.invitationRepository = invitationRepository;
        this.familyRepository = familyRepository;
        this.communicationsContextFacade = communicationsContextFacade;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(InvitationAcceptedEvent event) {
        log.info("User {} accepted invitation to family {}", event.getUserId(), event.getFamilyId());

        invitationRepository.findById(event.getInvitationId()).ifPresentOrElse(invitation -> {
            var familyName = familyRepository.findById(event.getFamilyId())
                    .map(Family::getName)
                    .orElse("el grupo");

            try {
                communicationsContextFacade.sendPushNotificationToUser(
                        invitation.getInvitedBy().getValue(),
                        NotificationType.FAMILY_GROUP_INVITATION.name(),
                        NotificationSource.FAMILY_GROUP.name(),
                        event.getFamilyId(),
                        "Invitación aceptada",
                        "Un usuario aceptó tu invitación al grupo " + familyName
                );
            } catch (Exception exception) {
                log.warn("Invitation accepted push notification could not be sent. invitationId={}, inviterUserId={}",
                        event.getInvitationId(), invitation.getInvitedBy().getValue(), exception);
            }
        }, () -> log.warn("Invitation not found while handling accepted event. invitationId={}", event.getInvitationId()));
    }
}

package com.resolum.intiva.platform.household.application.internal.eventhandlers;

import com.resolum.intiva.platform.household.domain.model.events.InvitationAcceptedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
public class InvitationAcceptedEventHandler {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(InvitationAcceptedEvent event) {
        log.info("User {} accepted invitation to family {}", event.getUserId(), event.getFamilyId());
    }
}

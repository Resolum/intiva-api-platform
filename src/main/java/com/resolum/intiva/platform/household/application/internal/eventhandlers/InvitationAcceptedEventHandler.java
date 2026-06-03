package com.resolum.intiva.platform.household.application.internal.eventhandlers;

import com.resolum.intiva.platform.household.domain.model.events.InvitationAcceptedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class InvitationAcceptedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationAcceptedEventHandler.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(InvitationAcceptedEvent event) {
        LOGGER.info("User {} accepted invitation to family {}", event.getUserId(), event.getFamilyId());
    }
}

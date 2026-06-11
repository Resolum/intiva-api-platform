package com.resolum.intiva.platform.household.application.internal.eventhandlers;

import com.resolum.intiva.platform.household.domain.model.events.FamilyCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event handler that reacts to family group creation events.
 */
@Slf4j
@Service
public class FamilyCreatedEventHandler {

    /**
     * Handles the FamilyCreatedEvent by logging the creation details.
     *
     * @param event the family created event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(FamilyCreatedEvent event) {
        log.info("Family group created successfully - name: {}, ownerId: {}",
                event.getFamilyName(), event.getOwnerId());
    }
}

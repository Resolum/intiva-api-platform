package com.resolum.intiva.platform.household.application.internal.eventhandlers;

import com.resolum.intiva.platform.household.domain.model.events.FamilyCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event handler that reacts to family group creation events.
 */
@Service
public class FamilyCreatedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyCreatedEventHandler.class);

    /**
     * Handles the FamilyCreatedEvent by logging the creation details.
     *
     * @param event the family created event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(FamilyCreatedEvent event) {
        LOGGER.info("Family group created successfully - name: {}, ownerId: {}",
                event.getFamilyName(), event.getOwnerId());
    }
}

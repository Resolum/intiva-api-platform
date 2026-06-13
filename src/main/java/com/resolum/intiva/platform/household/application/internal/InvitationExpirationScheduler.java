package com.resolum.intiva.platform.household.application.internal;

import com.resolum.intiva.platform.household.domain.model.valueobjects.InvitationStatus;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.InvitationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
public class InvitationExpirationScheduler {

    private final InvitationRepository invitationRepository;

    public InvitationExpirationScheduler(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @Scheduled(fixedDelay = 3600000)
    @Transactional
    public void expireInvitations() {
        log.debug("Running invitation expiration scheduler");

        var expiredInvitations = invitationRepository.findByStatusAndExpiresAtBefore(
                InvitationStatus.PENDING, LocalDateTime.now());

        for (var invitation : expiredInvitations) {
            try {
                invitation.expire();
                invitationRepository.save(invitation);
            } catch (IllegalStateException e) {
                log.warn("Could not expire invitation {}: {}", invitation.getId(), e.getMessage());
            }
        }

        if (!expiredInvitations.isEmpty()) {
            log.info("Expired {} invitation(s)", expiredInvitations.size());
        }
    }
}

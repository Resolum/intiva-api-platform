package com.resolum.intiva.platform.household.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeferredDeepLinkRepository extends JpaRepository<DeferredDeepLinkEntity, UUID> {

    Optional<DeferredDeepLinkEntity> findByInstallIdAndClaimedFalse(String installId);

    boolean existsByInstallIdAndInviteToken(String installId, String inviteToken);
}

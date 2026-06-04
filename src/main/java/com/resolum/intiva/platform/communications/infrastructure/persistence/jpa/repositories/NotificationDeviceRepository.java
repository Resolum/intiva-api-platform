package com.resolum.intiva.platform.communications.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.communications.domain.model.aggregates.NotificationDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for registered device tokens.
 */
@Repository
public interface NotificationDeviceRepository extends JpaRepository<NotificationDevice, Long> {

    /**
     * Finds one token registration by user id and exact device token.
     *
     * @param userId owner of the token
     * @param deviceToken token value
     * @return matching registration when it exists
     */
    Optional<NotificationDevice> findByUserIdAndDeviceToken(Long userId, String deviceToken);

    /**
     * Finds all token registrations of one user ordered by last update descending.
     *
     * @param userId owner of the token registrations
     * @return devices registered by the given user
     */
    List<NotificationDevice> findByUserIdOrderByUpdatedAtDesc(Long userId);

    /**
     * Finds all active token registrations of one user ordered by last update descending.
     *
     * @param userId owner of the token registrations
     * @return active devices registered by the given user
     */
    List<NotificationDevice> findByUserIdAndActiveTrueOrderByUpdatedAtDesc(Long userId);
}

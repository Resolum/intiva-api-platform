package com.resolum.intiva.platform.communications.infrastructure.persistence.jpa.repositories;

import com.resolum.intiva.platform.communications.domain.model.aggregates.Notification;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for persisted notifications.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Finds notifications by recipient user id ordered by creation date descending.
     *
     * @param recipientUserId recipient user identifier
     * @return notifications of the given recipient
     */
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId);

    /**
     * Finds notifications by recipient user id and status ordered by creation date descending.
     *
     * @param recipientUserId recipient user identifier
     * @param status notification status filter
     * @return filtered notifications of the given recipient
     */
    List<Notification> findByRecipientUserIdAndStatusOrderByCreatedAtDesc(Long recipientUserId, NotificationStatus status);
}

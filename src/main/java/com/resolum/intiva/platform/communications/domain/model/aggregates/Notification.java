package com.resolum.intiva.platform.communications.domain.model.aggregates;

import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationSource;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationStatus;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationType;
import com.resolum.intiva.platform.shared.domain.aggregates.AuditableAbstractAggregate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification extends AuditableAbstractAggregate<Notification> {

    @Column(nullable = false)
    private Long recipientUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationSource source;

    @Column(nullable = false)
    private Long sourceId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    public Notification(
            Long recipientUserId,
            NotificationType type,
            NotificationSource source,
            Long sourceId,
            String title,
            String message
    ) {
        this.recipientUserId = recipientUserId;
        this.type = type;
        this.source = source;
        this.sourceId = sourceId;
        this.title = title;
        this.message = message;
        this.status = NotificationStatus.UNREAD;
    }

    public void markAsRead() {
        this.status = NotificationStatus.READ;
    }
}

package com.resolum.intiva.platform.communications.domain.model.aggregates;

import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationSource;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationStatus;
import com.resolum.intiva.platform.communications.domain.model.valueobject.NotificationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void markAsRead() {

        // Arrange
        var notification = buildRecurringTransaction();

        // Act
        notification.markAsRead();

        // Assert
        assertEquals(NotificationStatus.READ, notification.getStatus());
    }

    private Notification buildRecurringTransaction(

    ) {
        return new Notification(
                1L,
                NotificationType.SPENDING_LIMIT_EXCEEDED,
                NotificationSource.SPENDING_LIMIT,
                5L,
                "Spending limit exceeded",
                "Your transaction of $100 has exceeded your spending limit of $50."
        );
    }
}
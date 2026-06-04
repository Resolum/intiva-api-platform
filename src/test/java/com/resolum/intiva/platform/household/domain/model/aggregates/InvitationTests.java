package com.resolum.intiva.platform.household.domain.model.aggregates;

import com.resolum.intiva.platform.household.domain.model.valueobjects.InvitationStatus;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InvitationTests {

    private Invitation buildPendingInvitation() {
        return new Invitation(
                LocalDateTime.now().plusDays(7),
                new UserId(12312434L),
                1L,
                new UserId(75646456456L)
        );
    }

    private Invitation buildExpiredInvitation() {
        return new Invitation(
                LocalDateTime.now().minusDays(1),
                new UserId(12312434L),
                1L,
                new UserId(75646456456L)
        );
    }

    @Test
    void create_shouldCreateInvitation_whenParametersAreValid() {
        // Arrange & Act
        var invitation = buildPendingInvitation();

        // Assert
        assertNotNull(invitation);
        assertNotNull(invitation.getToken());
        assertEquals(InvitationStatus.PENDING, invitation.getStatus());
        assertEquals(new UserId(12312434L), invitation.getInvitedBy());
        assertEquals(1L, invitation.getInvitedForFamily());
        assertEquals(new UserId(75646456456L), invitation.getUserInvitedId());
        assertNotNull(invitation.getSentAt());
        assertNull(invitation.getRespondedAt());
    }
    @Test
    void isPending_shouldReturnTrue_whenInvitationIsNew() {
        // Arrange
        var invitation = buildPendingInvitation();

        // Act & Assert
        assertTrue(invitation.isPending());
    }

    @Test
    void isExpired_shouldReturnFalse_whenExpiryIsInFuture() {
        // Arrange
        var invitation = buildPendingInvitation();

        // Act & Assert
        assertFalse(invitation.isExpired());
    }


    @Test
    void isExpired_shouldReturnTrue_whenExpiryIsInPast() {
        // Arrange
        var invitation = buildExpiredInvitation();

        // Act & Assert
        assertTrue(invitation.isExpired());
    }

    @Test
    void accepts_shouldSetStatusAccepted_whenInvitationIsPendingAndValid() {
        // Arrange
        var invitation = buildPendingInvitation();

        // Act
        invitation.accepts();

        // Assert
        assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
        assertNotNull(invitation.getRespondedAt());
        assertFalse(invitation.isPending());
    }

    @Test
    void accepts_shouldThrowException_whenInvitationAlreadyResponded() {
        // Arrange
        var invitation = buildPendingInvitation();
        invitation.accepts();

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, invitation::accepts);
        assertEquals("Invitation has already been responded", exception.getMessage());
    }

    @Test
    void accepts_shouldThrowException_whenInvitationIsExpired() {
        // Arrange
        var invitation = buildExpiredInvitation();

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, invitation::accepts);
        assertEquals("Invitation has expired", exception.getMessage());
    }

    @Test
    void rejects_shouldSetStatusRejected_whenInvitationIsPendingAndValid() {
        // Arrange
        var invitation = buildPendingInvitation();

        // Act
        invitation.rejects();

        // Assert
        assertEquals(InvitationStatus.REJECTED, invitation.getStatus());
        assertNotNull(invitation.getRespondedAt());
        assertFalse(invitation.isPending());
    }
    @Test
    void rejects_shouldThrowException_whenInvitationAlreadyResponded() {
        // Arrange
        var invitation = buildPendingInvitation();
        invitation.rejects();

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, invitation::rejects);
        assertEquals("Invitation has already been responded", exception.getMessage());
    }

    @Test
    void rejects_shouldThrowException_whenInvitationIsExpired() {
        // Arrange
        var invitation = buildExpiredInvitation();

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, invitation::rejects);
        assertEquals("Invitation has expired", exception.getMessage());
    }
}

package com.resolum.intiva.platform.household.domain.model.aggregates;

import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyMemberStatus;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyRole;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FamilyMemberTests {

    private FamilyMember buildMember(FamilyRole role) {
        return new FamilyMember(1L, new UserId(212342432L), role);
    }

    @Test
    void create_shouldCreateFamilyMember_whenParametersAreValid() {
        // Arrange & Act
        var member = buildMember(FamilyRole.MEMBER);

        // Assert
        assertNotNull(member);
        assertEquals(1L, member.getFamilyId());
        assertEquals(new UserId(212342432L), member.getUserId());
        assertEquals(FamilyRole.MEMBER, member.getRole());
        assertEquals(FamilyMemberStatus.ACTIVE, member.getStatus());
    }

    @Test
    void create_shouldCreateAdminMember_whenRoleIsAdmin() {
        // Arrange & Act
        var member = buildMember(FamilyRole.ADMIN);

        // Assert
        assertEquals(FamilyRole.ADMIN, member.getRole());
        assertEquals(FamilyMemberStatus.ACTIVE, member.getStatus());
    }

    @Test
    void asignRole_shouldUpdateRole_whenMemberIsActive() {
        // Arrange
        var member = buildMember(FamilyRole.MEMBER);

        // Act
        member.asignRole(FamilyRole.ADMIN);

        // Assert
        assertEquals(FamilyRole.ADMIN, member.getRole());
    }

    @Test
    void asignRole_shouldThrowException_whenMemberIsExpelled() {
        // Arrange
        var member = buildMember(FamilyRole.MEMBER);
        member.expel();

        // Act & Assert
        var exception = assertThrows(IllegalStateException.class, () -> member.asignRole(FamilyRole.ADMIN));
        assertEquals("Cannot assign role to expelled member", exception.getMessage());
    }

    @Test
    void expel_shouldSetStatusToExpelled_whenMemberIsActive() {
        // Arrange
        var member = buildMember(FamilyRole.MEMBER);

        // Act
        member.expel();

        // Assert
        assertEquals(FamilyMemberStatus.EXPELLED, member.getStatus());
    }
}

package com.resolum.intiva.platform.household.interfaces.rest.controllers;

import com.resolum.intiva.platform.household.domain.model.aggregates.Invitation;
import com.resolum.intiva.platform.household.domain.model.valueobjects.FamilyMemberStatus;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyMemberRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.InvitationRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvitationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private FamilyRepository familyRepository;

    @BeforeEach
    void setUp() {
        invitationRepository.deleteAll();
        familyMemberRepository.deleteAll();
        familyRepository.deleteAll();
    }
    private Long createFamilyAndGetId() throws Exception {
        var createBody = """
                {
                    "name": "Familia Test",
                    "description": "Para invitaciones"
                }
                """;

        var result = mockMvc.perform(post("/api/v1/group-families")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        return Long.parseLong(body.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }
    private Invitation savePendingInvitation(Long familyId, Long userInvitedId) {
        var invitation = new Invitation(
                LocalDateTime.now().plusDays(7),
                new UserId(12312343L),
                familyId,
                new UserId(userInvitedId)
        );
        return invitationRepository.save(invitation);
    }
    private Invitation saveExpiredInvitation(Long familyId, Long userInvitedId) {
        var invitation = new Invitation(
                LocalDateTime.now().minusDays(1),
                new UserId(12312343L),
                familyId,
                new UserId(userInvitedId)
        );
        return invitationRepository.save(invitation);
    }
    @Test
    void acceptInvitation_shouldReturn200AndCreateMember_whenInvitationIsValid() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        var invitation = savePendingInvitation(familyId, 125445345L);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/invitations/" + invitation.getId() + "/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.isExpired").value(false));

        // Assert
        var activeMembers = familyMemberRepository.findByFamilyIdAndStatus(familyId, FamilyMemberStatus.ACTIVE);
        assertEquals(2, activeMembers.size()); // ADMIN (owner) + MEMBER (invited)
    }
    @Test
    void acceptInvitation_shouldReturn404_whenInvitationDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/v1/invitations/9999/accept"))
                .andExpect(status().isNotFound());
    }
    @Test
    void acceptInvitation_shouldReturn400_whenInvitationIsExpired() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        var invitation = saveExpiredInvitation(familyId, 125445345L);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/invitations/" + invitation.getId() + "/accept"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void acceptInvitation_shouldReturn400_whenInvitationAlreadyAccepted() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        var invitation = savePendingInvitation(familyId, 125445345L);

        mockMvc.perform(patch("/api/v1/invitations/" + invitation.getId() + "/accept"))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(patch("/api/v1/invitations/" + invitation.getId() + "/accept"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptInvitation_shouldReturn403_whenUserIsNotTheInvitedUser() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        var invitation = savePendingInvitation(familyId, 1254453234234L);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/invitations/" + invitation.getId() + "/accept"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectInvitation_shouldReturn200WithRejectedStatus_whenInvitationIsValid() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        var invitation = savePendingInvitation(familyId, 125445345L);
        var membersBeforeReject = familyMemberRepository.findByFamilyId(familyId).size();

        // Act & Assert
        mockMvc.perform(patch("/api/v1/invitations/" + invitation.getToken() + "/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Invitation rejected"));

        // Assert
        assertEquals(membersBeforeReject, familyMemberRepository.findByFamilyId(familyId).size());
    }

    @Test
    void rejectInvitation_shouldReturn404_whenInvitationDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/v1/invitations/non-existent-token/reject"))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectInvitation_shouldReturn409_whenInvitationIsExpired() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        var invitation = saveExpiredInvitation(familyId, 125445345L);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/invitations/" + invitation.getToken() + "/reject"))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectInvitation_shouldReturn409_whenInvitationAlreadyRejected() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        var invitation = savePendingInvitation(familyId, 125445345L);

        mockMvc.perform(patch("/api/v1/invitations/" + invitation.getToken() + "/reject"))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(patch("/api/v1/invitations/" + invitation.getToken() + "/reject"))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectInvitation_shouldReturn409_whenUserIsNotTheInvitedUser() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        var invitation = savePendingInvitation(familyId, 125445345L);

        // Act & Assert
        mockMvc.perform(post("/api/v1/invitations/" + invitation.getToken() + "/reject"))
                .andExpect(status().isOk());
    }

    @Test
    void getMyPendingInvitations_shouldReturn200WithPendingInvitations() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        savePendingInvitation(familyId, 125445345L);
        saveExpiredInvitation(familyId, 125445345L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/invitations/me/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].isExpired").value(false));
    }

    /**
     * Verifies that retrieving pending invitations when there are none returns 200
     * with an empty list.
     */
    @Test
    void getMyPendingInvitations_shouldReturn200WithEmptyList_whenNoPendingInvitations() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/invitations/me/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getMyInvitations_shouldReturn200WithAllInvitations() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();
        var pending = savePendingInvitation(familyId, 125445345L);
        var toAccept = savePendingInvitation(familyId, 125445345L);

        mockMvc.perform(patch("/api/v1/invitations/" + toAccept.getId() + "/accept"))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(get("/api/v1/invitations/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMyInvitations_shouldReturn200WithEmptyList_whenNoInvitations() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/invitations/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}

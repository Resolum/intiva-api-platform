package com.resolum.intiva.platform.household.interfaces.rest.controllers;

import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyMemberRepository;
import com.resolum.intiva.platform.household.infrastructure.persistence.jpa.repositories.FamilyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FamilyMemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @BeforeEach
    void setUp() {
        familyMemberRepository.deleteAll();
        familyRepository.deleteAll();
    }

    private String createFamilyAndGetId() throws Exception {
        var createBody = """
                {
                    "name": "Familia Test",
                    "description": "Para miembros"
                }
                """;

        var result = mockMvc.perform(post("/api/v1/group-families")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        return result.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");
    }

    @Test
    void getMembers_shouldReturn200WithAdminMember_whenFamilyExists() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();

        // Act & Assert
        mockMvc.perform(get("/api/v1/families/" + familyId + "/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.familyId").value(Long.parseLong(familyId)))
                .andExpect(jsonPath("$.totalMembers").value(1))
                .andExpect(jsonPath("$.isEmpty").value(false))
                .andExpect(jsonPath("$.members[0].role").value("ADMIN"))
                .andExpect(jsonPath("$.members[0].status").value("ACTIVE"));
    }
    @Test
    void getMember_shouldReturn200_whenMemberExists() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();

        var membersResult = mockMvc.perform(get("/api/v1/families/" + familyId + "/members"))
                .andExpect(status().isOk())
                .andReturn();

        var memberId = membersResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act & Assert
        mockMvc.perform(get("/api/v1/families/" + familyId + "/members/" + memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Long.parseLong(memberId)))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
    @Test
    void getMember_shouldReturn404_whenMemberDoesNotExist() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();

        // Act & Assert
        mockMvc.perform(get("/api/v1/families/" + familyId + "/members/9999"))
                .andExpect(status().isNotFound());
    }
    @Test
    void assignRole_shouldReturn400_whenDemotingLastAdmin() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();

        var membersResult = mockMvc.perform(get("/api/v1/families/" + familyId + "/members"))
                .andExpect(status().isOk())
                .andReturn();

        var memberId = membersResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act & Assert
        var body = """
                { "role": "MEMBER" }
                """;

        mockMvc.perform(patch("/api/v1/families/" + familyId + "/members/" + memberId + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
    @Test
    void assignRole_shouldReturn400_whenRoleValueIsInvalid() throws Exception {
        // Arrange
        var familyId = createFamilyAndGetId();

        var membersResult = mockMvc.perform(get("/api/v1/families/" + familyId + "/members"))
                .andExpect(status().isOk())
                .andReturn();

        var memberId = membersResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        var body = """
                { "role": "" }
                """;

        // Act & Assert
        mockMvc.perform(patch("/api/v1/families/" + familyId + "/members/" + memberId + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
    @Test
    void assignRole_shouldReturn404_whenFamilyDoesNotExist() throws Exception {
        // Arrange
        var body = """
                { "role": "MEMBER" }
                """;

        // Act & Assert
        mockMvc.perform(patch("/api/v1/families/9999/members/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }
}

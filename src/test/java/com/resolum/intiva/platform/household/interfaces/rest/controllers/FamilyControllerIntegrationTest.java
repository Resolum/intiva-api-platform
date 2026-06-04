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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FamilyControllerIntegrationTest {

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

    @Test
    void createFamily_shouldReturn201AndCreateAdminMember_whenRequestIsValid() throws Exception {
        // Arrange
        var requestBody = """
                {
                    "name": "Familia García",
                    "description": "Grupo familiar principal"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/group-families")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Familia García"))
                .andExpect(jsonPath("$.description").value("Grupo familiar principal"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.id").isNotEmpty());

        // Assert family and admin member persisted
        assertEquals(1, familyRepository.count());
        assertEquals(1, familyMemberRepository.count());
    }
    @Test
    void createFamily_shouldReturn201_whenDescriptionIsOmitted() throws Exception {
        // Arrange
        var requestBody = """
                {
                    "name": "Familia Sin Descripción"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/group-families")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Familia Sin Descripción"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createFamily_shouldReturn400_whenNameIsBlank() throws Exception {
        // Arrange
        var requestBody = """
                {
                    "name": "",
                    "description": "Sin nombre"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/group-families")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getFamilyById_shouldReturn200_whenFamilyExists() throws Exception {
        // Arrange
        var createBody = """
                {
                    "name": "Familia Test",
                    "description": "Para consultar"
                }
                """;

        var createResult = mockMvc.perform(post("/api/v1/group-families")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var familyId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act & Assert
        mockMvc.perform(get("/api/v1/group-families/" + familyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Long.parseLong(familyId)))
                .andExpect(jsonPath("$.name").value("Familia Test"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
    @Test
    void getFamilyById_shouldReturn404_whenFamilyDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/group-families/9999"))
                .andExpect(status().isNotFound());
    }
}

package com.resolum.intiva.platform.savings.interfaces.rest.controllers;

import com.resolum.intiva.platform.savings.infrastructure.persistence.jpa.repositories.GoalContributionRepository;
import com.resolum.intiva.platform.savings.infrastructure.persistence.jpa.repositories.SavingGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SavingGoalsController.
 * Verifies the full HTTP lifecycle for creating, retrieving, updating,
 * deleting saving goals and registering contributions.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SavingGoalsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SavingGoalRepository savingGoalRepository;

    @Autowired
    private GoalContributionRepository goalContributionRepository;

    @BeforeEach
    void setUp() {
        goalContributionRepository.deleteAll();
        savingGoalRepository.deleteAll();
    }
    /**
     * Verifies that creating a saving goal with valid data returns 201
     * and persists the entity in the database.
     */
    @Test
    void createSavingGoal_shouldReturn201_whenRequestIsValid() throws Exception {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var requestBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "actorUserId": 1,
                    "title": "Vacaciones",
                    "targetAmount": 1000.00,
                    "currencyCode": "PEN",
                    "description": "Meta de ahorro para vacaciones",
                    "startsAt": "%s",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(Instant.now().toString(), deadline);

        // Act & Assert
        mockMvc.perform(post("/api/v1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Vacaciones"))
                .andExpect(jsonPath("$.status").value("INPROGRESS"))
                .andExpect(jsonPath("$.currentAmount").value(0));

        // Assert persisted
        assertEquals(1, savingGoalRepository.count());
    }

    /**
     * Verifies that creating a saving goal with a past deadline returns 400.
     */
    @Test
    void createSavingGoal_shouldReturn400_whenDeadlineIsInPast() throws Exception {
        // Arrange
        var pastDeadline = Instant.now().minus(1, ChronoUnit.DAYS).toString();
        var requestBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "actorUserId": 1,
                    "title": "Meta inválida",
                    "targetAmount": 500.00,
                    "currencyCode": "PEN",
                    "description": "Deadline pasado",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(pastDeadline);

        // Act & Assert
        mockMvc.perform(post("/api/v1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifies that creating a FAMILY saving goal without an ownerId returns 400.
     */
    @Test
    void createSavingGoal_shouldReturn400_whenFamilyGoalHasNoOwnerId() throws Exception {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var requestBody = """
                {
                    "ownerType": "FAMILY",
                    "title": "Meta familiar sin grupo",
                    "targetAmount": 2000.00,
                    "currencyCode": "PEN",
                    "description": "Sin ownerId",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        // Act & Assert
        mockMvc.perform(post("/api/v1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    /**
     * Verifies that retrieving a saving goal by a non-existent ID returns 404.
     */
    @Test
    void getSavingGoalById_shouldReturn404_whenGoalDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/saving-goals/9999"))
                .andExpect(status().isNotFound());
    }
    /**
     * Verifies that retrieving all saving goals for a user with no goals
     * returns 200 with an empty list.
     */
    @Test
    void getAllSavingGoalsByUserId_shouldReturn200WithEmptyList_whenUserHasNoGoals() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/saving-goals").param("userId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
    /**
     * Verifies that contributing to a non-existent saving goal returns 404.
     */
    @Test
    void contribute_shouldReturn404_whenSavingGoalDoesNotExist() throws Exception {
        // Arrange
        var requestBody = """
                {
                    "amount": 100.00,
                    "currencyCode": "PEN",
                    "contributorId": 1
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/saving-goals/9999/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifies that a valid contribution to an existing saving goal returns 201,
     * updates currentAmount, and persists the contribution.
     */
    @Test
    void contribute_shouldReturn201AndUpdateCurrentAmount_whenGoalExists() throws Exception {
        // Arrange — create the goal first
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "actorUserId": 1,
                    "title": "Meta con aporte",
                    "targetAmount": 1000.00,
                    "currencyCode": "PEN",
                    "description": "Meta de ahorro",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        var goalId = responseBody.replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act
        var contributeBody = """
                {
                    "amount": 300.00,
                    "currencyCode": "PEN",
                    "contributorId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/saving-goals/" + goalId + "/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contributeBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currentAmount").value(300.0));

        // Assert contribution persisted
        assertEquals(1, goalContributionRepository.count());
    }
    /**
     * Verifies that updating a saving goal before the deadline returns 200
     * with the updated fields.
     */
    @Test
    void updateSavingGoal_shouldReturn200_whenDeadlineHasNotPassed() throws Exception {
        // Arrange — create the goal first
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "actorUserId": 1,
                    "title": "Meta original",
                    "targetAmount": 1000.00,
                    "currencyCode": "PEN",
                    "description": "Descripción original",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act
        var updateBody = """
                {
                    "title": "Meta actualizada",
                    "description": "Descripción actualizada"
                }
                """;

        mockMvc.perform(patch("/api/v1/saving-goals/" + goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Meta actualizada"))
                .andExpect(jsonPath("$.description").value("Descripción actualizada"));
    }

    /**
     * Verifies that updating a non-existent saving goal returns 404.
     */
    @Test
    void updateSavingGoal_shouldReturn404_whenGoalDoesNotExist() throws Exception {
        // Arrange
        var updateBody = """
                {
                    "title": "Nuevo título"
                }
                """;

        // Act & Assert
        mockMvc.perform(patch("/api/v1/saving-goals/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());
    }
    /**
     * Verifies that deleting a saving goal before the deadline returns 204
     * and removes it from the database.
     */
    @Test
    void deleteSavingGoal_shouldReturn204_whenDeadlineHasNotPassed() throws Exception {
        // Arrange — create the goal first
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "actorUserId": 1,
                    "title": "Meta a eliminar",
                    "targetAmount": 500.00,
                    "currencyCode": "PEN",
                    "description": "Se eliminará",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/saving-goals/" + goalId))
                .andExpect(status().isNoContent());

        // Assert removed from DB
        assertTrue(savingGoalRepository.findById(Long.parseLong(goalId)).isEmpty());
    }

    /**
     * Verifies that deleting a non-existent saving goal returns 404.
     */
    @Test
    void deleteSavingGoal_shouldReturn404_whenGoalDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/saving-goals/9999"))
                .andExpect(status().isNotFound());
    }
    /**
     * Verifies that completing an existing saving goal returns 200
     * with status COMPLETED.
     */
    @Test
    void completeSavingGoal_shouldReturn200WithStatusCompleted_whenGoalExists() throws Exception {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "actorUserId": 1,
                    "title": "Meta a completar",
                    "targetAmount": 500.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act & Assert
        mockMvc.perform(patch("/api/v1/saving-goals/" + goalId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    /**
     * Verifies that completing an already completed saving goal returns 400.
     */
    @Test
    void completeSavingGoal_shouldReturn400_whenGoalIsAlreadyCompleted() throws Exception {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "actorUserId": 1,
                    "title": "Meta ya completa",
                    "targetAmount": 500.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(patch("/api/v1/saving-goals/" + goalId + "/complete"))
                .andExpect(status().isOk());

        // Act & Assert — second complete should fail
        mockMvc.perform(patch("/api/v1/saving-goals/" + goalId + "/complete"))
                .andExpect(status().isBadRequest());
    }
}

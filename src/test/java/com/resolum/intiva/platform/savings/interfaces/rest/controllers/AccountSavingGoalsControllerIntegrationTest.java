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
 * Integration tests for AccountSavingGoalsController.
 * Verifies endpoints scoped under /api/v1/users/{userId}/saving-goals,
 * including ownership enforcement, contributions, updates, and deletions.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountSavingGoalsControllerIntegrationTest {

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
     * Verifies that creating a saving goal via the user-scoped endpoint returns 201,
     * and the actorUserId is automatically set to the userId path variable.
     */
    @Test
    void createSavingGoal_shouldReturn201AndSetActorUserId_whenRequestIsValid() throws Exception {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var requestBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Meta de usuario",
                    "targetAmount": 800.00,
                    "currencyCode": "PEN",
                    "description": "Meta personal",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/42/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.actorUserId").value(42))
                .andExpect(jsonPath("$.status").value("INPROGRESS"));

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
                    "title": "Meta inválida",
                    "targetAmount": 500.00,
                    "currencyCode": "PEN",
                    "description": "Deadline pasado",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(pastDeadline);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    /**
     * Verifies that retrieving saving goals for a user with no goals returns
     * 200 with an empty list.
     */
    @Test
    void getAllSavingGoals_shouldReturn200WithEmptyList_whenUserHasNoGoals() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/users/99/saving-goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    /**
     * Verifies that only the saving goals belonging to the requested user are returned.
     */
    @Test
    void getAllSavingGoals_shouldReturnOnlyGoalsBelongingToUser_whenMultipleUsersExist() throws Exception {
        // Arrange — create goal for user 1
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var bodyUser1 = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Meta usuario 1",
                    "targetAmount": 500.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        mockMvc.perform(post("/api/v1/users/1/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyUser1))
                .andExpect(status().isCreated());

        // Act & Assert — user 2 should see empty list
        mockMvc.perform(get("/api/v1/users/2/saving-goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        // user 1 should see their goal
        mockMvc.perform(get("/api/v1/users/1/saving-goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Meta usuario 1"));
    }
    /**
     * Verifies that a user cannot retrieve a saving goal that belongs to another user.
     */
    @Test
    void getSavingGoalById_shouldReturn404_whenGoalBelongsToAnotherUser() throws Exception {
        // Arrange — create goal for user 10
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Meta de usuario 10",
                    "targetAmount": 300.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/users/10/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act & Assert — user 20 tries to access user 10's goal
        mockMvc.perform(get("/api/v1/users/20/saving-goals/" + goalId))
                .andExpect(status().isNotFound());
    }
    /**
     * Verifies that a contribution via the user-scoped endpoint sets contributorId
     * from the path variable and updates currentAmount.
     */
    @Test
    void contribute_shouldReturn201AndSetContributorIdFromPath_whenGoalExists() throws Exception {
        // Arrange — create goal
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Meta con aporte",
                    "targetAmount": 1000.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/users/5/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act — contribute without sending contributorId in body (taken from path)
        var contributeBody = """
                {
                    "amount": 250.00,
                    "currencyCode": "PEN"
                }
                """;

        mockMvc.perform(post("/api/v1/users/5/saving-goals/" + goalId + "/contributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contributeBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currentAmount").value(250.0));

        // Assert contribution saved
        assertEquals(1, goalContributionRepository.count());
    }
    /**
     * Verifies that a user can update their own saving goal before the deadline.
     */
    @Test
    void updateSavingGoal_shouldReturn200_whenUserOwnsGoalAndDeadlineHasNotPassed() throws Exception {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Título original",
                    "targetAmount": 600.00,
                    "currencyCode": "PEN",
                    "description": "Descripción original",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/users/7/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act
        var updateBody = """
                {
                    "title": "Título actualizado",
                    "newTargetAmount": 1200.00
                }
                """;

        mockMvc.perform(patch("/api/v1/users/7/saving-goals/" + goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título actualizado"))
                .andExpect(jsonPath("$.targetAmount").value(1200.0));
    }

    /**
     * Verifies that a user cannot update a saving goal that belongs to another user.
     */
    @Test
    void updateSavingGoal_shouldReturn404_whenGoalBelongsToAnotherUser() throws Exception {
        // Arrange — goal belongs to user 8
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Meta usuario 8",
                    "targetAmount": 300.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/users/8/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act — user 9 tries to update user 8's goal
        var updateBody = """
                { "title": "Intento de robo" }
                """;

        mockMvc.perform(patch("/api/v1/users/9/saving-goals/" + goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());
    }
    /**
     * Verifies that a user can delete their own saving goal before the deadline.
     */
    @Test
    void deleteSavingGoal_shouldReturn204_whenUserOwnsGoalAndDeadlineHasNotPassed() throws Exception {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Meta a eliminar",
                    "targetAmount": 400.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/users/3/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/3/saving-goals/" + goalId))
                .andExpect(status().isNoContent());

        assertTrue(savingGoalRepository.findById(Long.parseLong(goalId)).isEmpty());
    }

    /**
     * Verifies that a user cannot delete a saving goal that belongs to another user.
     */
    @Test
    void deleteSavingGoal_shouldReturn404_whenGoalBelongsToAnotherUser() throws Exception {
        // Arrange — goal belongs to user 11
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Meta usuario 11",
                    "targetAmount": 400.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/users/11/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act — user 12 tries to delete user 11's goal
        mockMvc.perform(delete("/api/v1/users/12/saving-goals/" + goalId))
                .andExpect(status().isNotFound());
    }
    /**
     * Verifies that completing a saving goal via the user-scoped endpoint returns 200
     * with status COMPLETED.
     */
    @Test
    void completeSavingGoal_shouldReturn200WithStatusCompleted() throws Exception {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Meta a completar",
                    "targetAmount": 500.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/users/6/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        // Act & Assert
        mockMvc.perform(patch("/api/v1/users/6/saving-goals/" + goalId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    /**
     * Verifies that uncompleting a completed saving goal returns 200
     * with status UNCOMPLETED.
     */
    @Test
    void uncompleteSavingGoal_shouldReturn200WithStatusUncompleted_whenGoalIsCompleted() throws Exception {
        // Arrange
        var deadline = Instant.now().plus(30, ChronoUnit.DAYS).toString();
        var createBody = """
                {
                    "ownerType": "INDIVIDUAL",
                    "title": "Meta a descompletar",
                    "targetAmount": 500.00,
                    "currencyCode": "PEN",
                    "description": "Meta",
                    "deadline": "%s",
                    "categoryId": 1
                }
                """.formatted(deadline);

        var createResult = mockMvc.perform(post("/api/v1/users/6/saving-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        var goalId = createResult.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(patch("/api/v1/users/6/saving-goals/" + goalId + "/complete"))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(patch("/api/v1/users/6/saving-goals/" + goalId + "/uncomplete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNCOMPLETED"));
    }
}

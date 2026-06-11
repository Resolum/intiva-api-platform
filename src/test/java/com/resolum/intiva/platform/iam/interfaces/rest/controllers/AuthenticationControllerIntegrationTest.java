package com.resolum.intiva.platform.iam.interfaces.rest.controllers;

import com.resolum.intiva.platform.iam.infrastructure.hashing.bcrypt.services.HashingServiceImpl;
import com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("hashingServiceImpl")
    private HashingServiceImpl hashingService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void signUpEndpoint_shouldCreateUser() throws Exception {

        // Arrange
        var requestBody = """
            {
                "email": "test@email.com",
                "password": "P@ssw0rd!"
            }
        """;

        // Act
        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("test@email.com"))
                .andExpect(jsonPath("$.id").isNotEmpty());

        // Assert
        assertTrue(userRepository.existsUserByEmail_Email("test@email.com"));
    }
}

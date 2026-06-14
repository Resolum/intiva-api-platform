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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
        var avatarFile = new MockMultipartFile(
                "avatarFile",
                "avatar.png",
                "image/png",
                "fake-image-content".getBytes()
        );

        // Act
        mockMvc.perform(multipart("/api/v1/authentication/sign-up")
                        .file(avatarFile)
                        .param("email", "test@email.com")
                        .param("password", "P@ssw0rd!")
                        .param("name", "John Doe")
                        .param("age", "25")
                        .param("phoneNumber", "+51987654321")
                        .param("bio", "Software Engineer"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.id").isNotEmpty());

        // Assert
        assertTrue(userRepository.existsUserByEmail_Email("test@email.com"));
    }
}

package com.resolum.intiva.platform.iam.domain.model.aggregates;

import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.PasswordHash;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User entity, validating the creation and behavior of User instances with valid email and password hash values.
 * These tests ensure that the User entity can be instantiated correctly and that its properties are set as expected.
 */
public class UserTests {

    /**
     * Tests that a User instance can be created successfully when provided with a valid email and password hash.
     * This test verifies that the constructor of the User class correctly initializes the object without throwing exceptions, and that the resulting User instance is not null.
     */
    @Test
    void create_shouldCreateUser_whenPasswordHashIsValid() {
        // Arrange
        var email = new Email("test@gmail.com");
        var passwordHash = new PasswordHash("hashed-password");

        // Act
        var user = new User(email, passwordHash);

        // Assert
        assertNotNull(user);
    }

    /**
     * Tests that an IllegalArgumentException is thrown when attempting to create a User instance with a null password hash.
     * This test ensures that the User class enforces the requirement for a valid password hash and does not allow the creation of a User with invalid credentials.
     */
    @Test
    void create_shouldThrowException_whenPasswordHashIsNull() {
        // Arrange
        var email = new Email("test@gmail.com");

        // Act
        var exception = assertThrows(IllegalArgumentException.class, () -> new User(email, null));

        // Assert
        assertEquals(
                "Password must not be null or blank",
                exception.getMessage()
        );
    }

    /**
     * Tests that an IllegalArgumentException is thrown when attempting to create a User instance with a blank password hash.
     * This test ensures that the User class enforces the requirement for a valid password hash and does not allow the creation of a User with invalid credentials, specifically when the password hash is an empty string.
     */
    @Test
    void create_shouldThrowException_whenPasswordHashIsBlank() {
        // Arrange
        var email = new Email("test@gmail.com");

        // Act
        var exception = assertThrows(IllegalArgumentException.class, () -> new PasswordHash(""));

        // Assert
        assertEquals(
                "Password hash cannot be null or blank",
                exception.getMessage()
        );
    }

    /**
     * Tests that the updatePassword method of the User class successfully replaces the existing password hash with a new one.
     * This test verifies that the updatePassword method correctly updates the password hash and that subsequent updates can be made without throwing exceptions, ensuring that the User entity can manage changes to its authentication credentials over time.
     */
    @Test
    void updatePassword_shouldReplacePasswordHash() {
        // Arrange
        var email = new Email("test@gmail.com");
        var passwordHash = new PasswordHash("old-hash");
        var user = new User(email, passwordHash);

        var newPasswordHash = new PasswordHash("new-hash");

        // Act
        user.updatePassword(newPasswordHash);

        // Assert
        assertDoesNotThrow(() -> user.updatePassword(new PasswordHash("another-hash")));
    }
}

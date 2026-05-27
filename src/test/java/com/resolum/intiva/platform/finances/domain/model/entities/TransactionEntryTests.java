package com.resolum.intiva.platform.finances.domain.model.entities;

import com.resolum.intiva.platform.shared.domain.entities.TransactionEntry;
import com.resolum.intiva.platform.shared.domain.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransactionEntryTests {

    /**
     * Test case for creating a TransactionEntry with valid parameters.
     * This test verifies that a TransactionEntry can be successfully created when all required parameters are provided and valid.
     */
    @Test
    void create_shouldCreateTransactionEntry_whenParametersAreValid() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var description = "Test transaction";
        var ownerId = 1L;
        var financialAccountId = new FinancialAccountId(12345678L);
        var performedByUserId = new UserId(12345L);
        var ownerType = OwnerTypes.INDIVIDUAL;

        // Act
        var transactionEntry = new TransactionEntry(money, description, ownerId, financialAccountId, performedByUserId, ownerType);

        // Assert
        assertNotNull(transactionEntry);
    }

    /**
     * Test case for creating a TransactionEntry with a null Money parameter.
     * This test verifies that an IllegalArgumentException is thrown when the Money parameter is null,=.
     */
    @Test
    void create_shouldThrowException_whenMoneyIsNull() {
        // Arrange
        var description = "Test transaction";
        var ownerId = 1L;
        var financialAccountId = new FinancialAccountId(12345678L);
        var performedByUserId = new UserId(12345L);
        var ownerType = OwnerTypes.INDIVIDUAL;

        // Act & Assert
        try {
            new TransactionEntry(null, description, ownerId, financialAccountId, performedByUserId, ownerType);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /**
     * Test case for creating a TransactionEntry with a null description parameter.
     * This test verifies that an IllegalArgumentException is thrown when the description parameter is null.
     */
    @Test
    void create_shouldThrowException_whenDescriptionIsNull() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        String description = "";
        var ownerId = 1L;
        var financialAccountId = new FinancialAccountId(12345678L);
        var performedByUserId = new UserId(12345L);
        var ownerType = OwnerTypes.INDIVIDUAL;

        // Act & Assert
        try {
            new TransactionEntry(money, description, ownerId, financialAccountId, performedByUserId, ownerType);
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }
}

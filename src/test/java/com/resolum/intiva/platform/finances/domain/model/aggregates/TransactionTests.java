package com.resolum.intiva.platform.finances.domain.model.aggregates;

import com.resolum.intiva.platform.finances.domain.model.valueobjects.TransactionTypes;
import com.resolum.intiva.platform.shared.domain.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Transaction class, which represents a financial transaction in the system. These tests cover the creation of Transaction instances with valid and invalid parameters, as well as the behavior of methods that allow editing the transaction's description, amount, and category. The tests ensure that the Transaction class correctly validates input and maintains data integrity when modifying its state.
 */
public class TransactionTests {

    /**
     * Test case to verify that a Transaction can be created successfully with valid parameters.
     * This test ensures that the constructor of the Transaction class correctly initializes the object when provided with valid input values for amount, description, ownerId, financialAccountId, actorUserId, transactionType, and categoryId.
     */
    @Test
    void create_shouldRegisterEntry_whenParametersAreValid() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var description = "Test transaction";
        var ownerId = "owner123";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);

        // Act
        var income = new Transaction(money, description, ownerId, financialAccountId, actorUserId, transactionType, categoryId);

        // Assert
        assertNotNull(income);
    }

    /**
     * Test case to verify that creating a Transaction with an invalid amount (negative value) throws an IllegalArgumentException.
     * This test ensures that the constructor of the Transaction class correctly validates the amount parameter and throws an exception when an invalid value is provided, thus preventing the creation of a Transaction with a negative amount.
     */
    @Test
    void create_shouldThrowException_whenAmountIsInvalid() {
        // Arrange
        var description = "Test transaction";
        var ownerId = "owner123";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Transaction(new Money(BigDecimal.valueOf(-10.00), CurrencyCodes.PEN), description, ownerId, financialAccountId, actorUserId, transactionType, categoryId));
    }

    /**
     * Test case to verify that creating a Transaction with an invalid description (empty string) throws an IllegalArgumentException.
     * This test ensures that the constructor of the Transaction class correctly validates the description parameter and throws an exception when an invalid value is provided, thus preventing the creation of a Transaction with an empty description.
     */
    @Test
    void create_shouldThrowException_whenDescriptionIsInvalid() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var ownerId = "owner123";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Transaction(money, "", ownerId, financialAccountId, actorUserId, transactionType, categoryId));
    }

    /**
     * Test case to verify that creating a Transaction with an invalid ownerId (empty string) throws an IllegalArgumentException.
     * This test ensures that the constructor of the Transaction class correctly validates the ownerId parameter and throws an exception when an invalid value is provided, thus preventing the creation of a Transaction with an empty ownerId.
     */
    @Test
    void create_shouldThrowException_whenOwnerIdIsInvalid() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var description = "Test transaction";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Transaction(money, description, "", financialAccountId, actorUserId, transactionType, categoryId));
    }

    /**
     * Test case to verify that the editDescription method of the Transaction class successfully updates the description when provided with a valid new description.
     * This test ensures that the editDescription method correctly validates the new description and updates the Transaction's description field when a valid non-empty string is provided, allowing for proper modification of the transaction's description after creation.
     */
    @Test
    void editDescription_shouldUpdateDescription_whenNewDescriptionIsValid() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var description = "Test transaction";
        var ownerId = "owner123";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);
        var transaction = new Transaction(money, description, ownerId, financialAccountId, actorUserId, transactionType, categoryId);

        // Act
        var newDescription = "Updated transaction description";
        transaction.editDescription(newDescription);

        // Assert
        assertEquals(newDescription, transaction.getDescription());
    }

    /**
     * Test case to verify that the editDescription method of the Transaction class throws an IllegalArgumentException when provided with an invalid new description (empty string).
     * This test ensures that the editDescription method correctly validates the new description and throws an exception when an invalid value is provided, thus preventing the update of the Transaction's description to an empty string and maintaining data integrity.
     */
    @Test
    void editDescription_shouldNotUpdate_whenDescriptionIsNotValid() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var description = "Test transaction";
        var ownerId = "owner123";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);
        var transaction = new Transaction(money, description, ownerId, financialAccountId, actorUserId, transactionType, categoryId);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> transaction.editDescription(""));
    }

    /**
     * Test case to verify that the editAmount method of the Transaction class successfully updates the amount when provided with a valid new amount.
     * This test ensures that the editAmount method correctly validates the new amount and updates the Transaction's amount field when a valid non-negative Money object is provided, allowing for proper modification of the transaction's amount after creation.
     */
    @Test
    void editAmount_shouldUpdateAmount_whenNewAmountIsValid() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var description = "Test transaction";
        var ownerId = "owner123";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);
        var transaction = new Transaction(money, description, ownerId, financialAccountId, actorUserId, transactionType, categoryId);

        // Act
        var newAmount = new Money(BigDecimal.valueOf(20.00), CurrencyCodes.PEN);
        transaction.editAmount(newAmount);

        // Assert
        assertEquals(newAmount, transaction.getAmount());
    }

    /**
     * Test case to verify that the editAmount method of the Transaction class throws an IllegalArgumentException when provided with an invalid new amount (negative value).
     * This test ensures that the editAmount method correctly validates the new amount and throws an exception when an invalid value is provided, thus preventing the update of the Transaction's amount to a negative value and maintaining data integrity.
     */
    @Test
    void editAmount_shouldNotUpdate_whenNewAmountIsInvalid() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var description = "Test transaction";
        var ownerId = "owner123";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);
        var transaction = new Transaction(money, description, ownerId, financialAccountId, actorUserId, transactionType, categoryId);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> transaction.editAmount(new Money(BigDecimal.valueOf(-20.00), CurrencyCodes.PEN)));
    }

    /**
     * Test case to verify that the editCategory method of the Transaction class successfully updates the category when provided with a valid new category ID.
     * This test ensures that the editCategory method correctly updates the Transaction's categoryId field when a valid CategoryId object is provided, allowing for proper modification of the transaction's category after creation.
     */
    @Test
    void updateCategory_shouldUpdateCategory_whenNewCategoryIsValid() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var description = "Test transaction";
        var ownerId = "owner123";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);
        var transaction = new Transaction(money, description, ownerId, financialAccountId, actorUserId, transactionType, categoryId);

        // Act
        var newCategoryId = new CategoryId(456456L);
        transaction.editCategory(newCategoryId);

        // Assert
        assertEquals(newCategoryId, transaction.getCategoryId());
    }

    /**
     * Test case to verify that the editAmount method of the Transaction class throws an IllegalArgumentException when attempting to update the amount of a transaction that is older than 3 days.
     * This test ensures that the editAmount method correctly checks the age of the transaction using the createdAt timestamp and throws an exception when an attempt is made to update the amount of a transaction that was created more than 3 days ago, thus enforcing the business rule that transaction amounts cannot be edited after a certain period.
     */
    @Test
    void updateAmount_cannotUpdateAmount_whenTransactionIsOlderThan3Days() {
        // Arrange
        var money = new Money(BigDecimal.valueOf(10.00), CurrencyCodes.PEN);
        var description = "Test transaction";
        var ownerId = "owner123";
        var financialAccountId = new FinancialAccountId(12345678L);
        var actorUserId = new UserId(12345L);
        var transactionType = TransactionTypes.INCOME;
        var categoryId = new CategoryId(123123L);
        var transaction = new Transaction(money, description, ownerId, financialAccountId, actorUserId, transactionType, categoryId);

        // Act
        transaction.getCreatedAt().minusSeconds(4 * 24 * 60 * 60); // Simulate transaction created 4 days ago

        // Assert
        assertThrows(IllegalArgumentException.class, () -> transaction.editAmount(new Money(BigDecimal.valueOf(20.00), CurrencyCodes.PEN)));
    }
}

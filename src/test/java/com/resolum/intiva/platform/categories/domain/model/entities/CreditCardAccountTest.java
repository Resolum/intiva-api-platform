package com.resolum.intiva.platform.categories.domain.model.entities;

import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This Test is to verify that the applyTransaction method of the CreditCardAccount class works correctly.
 */
class CreditCardAccountTest {

    /**
     * This Test is to verify that the applyTransaction method of the CreditCardAccount class works correctly.
     */
    @Test
    void applyTransaction() {
        // ARRANGE
        var account = new CreditCardAccount(
                new AccountName("Tarjeta de Crédito BCP"),
                new Money(BigDecimal.ZERO, CurrencyCodes.PEN),
                BigDecimal.valueOf(5000),
                new Institution("BCP"),
                1L
        );

        // ACT
        account.applyTransaction(
                new Money(BigDecimal.valueOf(5000), CurrencyCodes.PEN),
                TransactionTypes.EXPENSE
        );

        // ASSERT
        assertEquals(
                BigDecimal.valueOf(5000),
                account.getCurrentAmount().getAmount()
        );
    }
}
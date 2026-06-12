package com.resolum.intiva.platform.categories.domain.model.entities;

import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.Institution;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import com.resolum.intiva.platform.shared.domain.valueobjects.TransactionTypes;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CashAccountTest {

    /**
     * This Test is to verify that the applyTransaction method of the FinancialAccount class works correctly.
     */
    @Test
    void applyTransaction() {

        // ARRANGE
        var account = new DebitCardAccount(
                new AccountName("Cuenta BCP"),
                new Money(BigDecimal.valueOf(100), CurrencyCodes.PEN),
                new Institution("BCP"),
                1L
        );

        // ACT
        account.applyTransaction(
                new Money(BigDecimal.valueOf(20), CurrencyCodes.PEN),
                TransactionTypes.EXPENSE
        );

        // ASSERT
        assertEquals(
                BigDecimal.valueOf(80),
                account.getCurrentAmount().getAmount()
        );
    }
}
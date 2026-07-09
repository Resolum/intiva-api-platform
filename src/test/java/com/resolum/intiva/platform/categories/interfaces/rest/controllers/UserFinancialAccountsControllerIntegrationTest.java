package com.resolum.intiva.platform.categories.interfaces.rest.controllers;

import com.resolum.intiva.platform.categories.domain.model.entities.WalletAccount;
import com.resolum.intiva.platform.categories.domain.model.valueobjects.AccountName;
import com.resolum.intiva.platform.categories.infraestructure.persistence.jpa.repositories.FinancialAccountRepository;
import com.resolum.intiva.platform.iam.domain.model.aggregates.User;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.Email;
import com.resolum.intiva.platform.iam.domain.model.valueobjects.PasswordHash;
import com.resolum.intiva.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.resolum.intiva.platform.shared.domain.valueobjects.CurrencyCodes;
import com.resolum.intiva.platform.shared.domain.valueobjects.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserFinancialAccountsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialAccountRepository financialAccountRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        financialAccountRepository.deleteAll();
        userRepository.deleteAll();

        var user = new User(
                new Email("testuser@example.com"),
                new PasswordHash("$2a$10$hashedpassword")
        );
        user = userRepository.save(user);
        userId = user.getId();
    }

    @Test
    void getFinancialAccounts_shouldReturn200WithDefaultAccount_whenNoCustomAccounts() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}/financial-accounts", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Efectivo"))
                .andExpect(jsonPath("$[0].accountType").value("CASH"));
    }

    @Test
    void getFinancialAccounts_shouldReturn200WithAccounts_whenAccountsExist() throws Exception {
        var account = new WalletAccount(
                new AccountName("Mi Billetera"),
                new Money(BigDecimal.valueOf(500), CurrencyCodes.PEN),
                null,
                userId
        );
        financialAccountRepository.save(account);

        mockMvc.perform(get("/api/v1/users/{userId}/financial-accounts", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Efectivo"))
                .andExpect(jsonPath("$[0].accountType").value("CASH"))
                .andExpect(jsonPath("$[1].name").value("Mi Billetera"))
                .andExpect(jsonPath("$[1].isActive").value(true));
    }

    @Test
    void createFinancialAccount_shouldReturn201_whenCreatingWallet() throws Exception {
        var requestBody = """
                {
                    "name": "Nueva Billetera",
                    "accountType": "WALLET",
                    "currencyCode": "PEN",
                    "initialAmount": 1000.00
                }
                """;

        mockMvc.perform(post("/api/v1/users/{userId}/financial-accounts", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nueva Billetera"))
                .andExpect(jsonPath("$.accountType").value("WALLET"))
                .andExpect(jsonPath("$.currencyCode").value("PEN"))
                .andExpect(jsonPath("$.currentAmount").value(1000.00))
                .andExpect(jsonPath("$.isActive").value(true));

        assertEquals(2, financialAccountRepository.count());
    }

    @Test
    void createFinancialAccount_shouldReturn201_whenCreatingDebitCard() throws Exception {
        var requestBody = """
                {
                    "name": "Debito BCP",
                    "accountType": "DEBITCARD",
                    "currencyCode": "PEN",
                    "initialAmount": 2000.00,
                    "institution": "BCP"
                }
                """;

        mockMvc.perform(post("/api/v1/users/{userId}/financial-accounts", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountType").value("DEBITCARD"))
                .andExpect(jsonPath("$.institution").value("BCP"));
    }

    @Test
    void createFinancialAccount_shouldReturn201_whenCreatingCreditCard() throws Exception {
        var requestBody = """
                {
                    "name": "Credito BCP",
                    "accountType": "CREDITCARD",
                    "currencyCode": "PEN",
                    "initialAmount": 0.00,
                    "creditLimit": 5000.00,
                    "institution": "BCP"
                }
                """;

        mockMvc.perform(post("/api/v1/users/{userId}/financial-accounts", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountType").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.creditLimit").value(5000.00));
    }

    @Test
    void createFinancialAccount_shouldReturn404_whenUserNotFound() throws Exception {
        var requestBody = """
                {
                    "name": "Cuenta",
                    "accountType": "WALLET",
                    "initialAmount": 100.00
                }
                """;

        mockMvc.perform(post("/api/v1/users/9999/financial-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void createFinancialAccount_shouldReturn400_whenNameIsBlank() throws Exception {
        var requestBody = """
                {
                    "name": "",
                    "accountType": "WALLET",
                    "initialAmount": 100.00
                }
                """;

        mockMvc.perform(post("/api/v1/users/{userId}/financial-accounts", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFinancialAccount_shouldReturn200_whenUpdatingName() throws Exception {
        var account = new WalletAccount(
                new AccountName("Original"),
                new Money(BigDecimal.valueOf(100), CurrencyCodes.PEN),
                null,
                userId
        );
        account = financialAccountRepository.save(account);
        var accountId = account.getId();

        var requestBody = """
                {
                    "name": "Actualizada"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/{userId}/financial-accounts/{accountId}", userId, accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Actualizada"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void updateFinancialAccount_shouldReturn200_whenDeactivating() throws Exception {
        var account = new WalletAccount(
                new AccountName("Cuenta"),
                new Money(BigDecimal.valueOf(100), CurrencyCodes.PEN),
                null,
                userId
        );
        account = financialAccountRepository.save(account);
        var accountId = account.getId();

        var requestBody = """
                {
                    "isActive": false
                }
                """;

        mockMvc.perform(patch("/api/v1/users/{userId}/financial-accounts/{accountId}", userId, accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void updateFinancialAccount_shouldReturn404_whenUserNotFound() throws Exception {
        var requestBody = """
                {
                    "name": "Nuevo nombre"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/9999/financial-accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFinancialAccount_shouldReturn404_whenAccountNotFound() throws Exception {
        var requestBody = """
                {
                    "name": "Nuevo nombre"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/{userId}/financial-accounts/9999", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFinancialAccount_shouldReturn400_whenNoFieldsProvided() throws Exception {
        var requestBody = """
                {
                }
                """;

        mockMvc.perform(patch("/api/v1/users/{userId}/financial-accounts/1", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFinancialAccount_shouldReturn403_whenAccountBelongsToDifferentUser() throws Exception {
        var otherUser = new User(
                new Email("otheruser@example.com"),
                new PasswordHash("$2a$10$otherhash")
        );
        otherUser = userRepository.save(otherUser);

        var account = new WalletAccount(
                new AccountName("Cuenta de otro"),
                new Money(BigDecimal.valueOf(100), CurrencyCodes.PEN),
                null,
                otherUser.getId()
        );
        account = financialAccountRepository.save(account);

        var requestBody = """
                {
                    "name": "Intento de update"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/{userId}/financial-accounts/{accountId}", userId, account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }
}
